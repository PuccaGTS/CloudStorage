package ru.netology.cloudstorage.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.cloudstorage.dto.file.FileDto;
import ru.netology.cloudstorage.dto.file.UpdateFileNameDto;
import ru.netology.cloudstorage.exception.BadRequestException;
import ru.netology.cloudstorage.exception.InternalServerException;
import ru.netology.cloudstorage.exception.UnauthorizedException;
import ru.netology.cloudstorage.model.File;
import ru.netology.cloudstorage.model.User;
import ru.netology.cloudstorage.repository.FileRepository;
import ru.netology.cloudstorage.repository.impl.AuthorizationRepositoryImpl;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FileService {
    private final FileRepository fileRepository;
    private final UserService userService;
    private final AuthorizationRepositoryImpl authorizationRepository;

    public List<FileDto> getAllFiles(String token, int limit){
        String email = authorizationRepository.getUserEmail(token)
                .orElseThrow(()->new UnauthorizedException("Unauthorized error, please login again"));
        User user = userService.loadUserByUsername(email);
        List<File> file = fileRepository.getFileByUserId(user.getId())
                .orElseThrow(()->new InternalServerException("Server error, can not get List of files"));
        return file.stream()
                .map(o -> new FileDto(o.getFilename(), o.getSize()))
                .collect(Collectors.toList());
    }

    public File save(String token, String filename, MultipartFile multipartFile) throws IOException {
        if (multipartFile == null || multipartFile.isEmpty()){
            throw new BadRequestException("Error input data, file cant be null or empty");
        }
        String email = authorizationRepository.getUserEmail(token)
                .orElseThrow(()->new UnauthorizedException("Unauthorized error, please login again"));
        User user = userService.loadUserByUsername(email);
        File file = File.builder()
                .filename(filename)
                .size(multipartFile.getSize())
                .fileData(multipartFile.getBytes())
                .user(user)
                .build();
        return fileRepository.save(file);
    }

    public void delete(String token, String filename) {
        authorizationRepository.getUserEmail(token)
                .orElseThrow(()->new UnauthorizedException("Unauthorized error, please login again"));
        File file = fileRepository.findByFilename(filename)
                .orElseThrow(()->new BadRequestException("Error input data " + filename));
        try {
            fileRepository.delete(file);
        } catch (Exception e){
            throw new InternalServerException("Server error, can not delete file: " + filename);
        }
    }

    public File update(String token, String filename, UpdateFileNameDto newFilenameDto) {
        authorizationRepository.getUserEmail(token)
                .orElseThrow(()->new UnauthorizedException("Unauthorized error, please login again"));
        File file = fileRepository.findByFilename(filename)
                .orElseThrow(()->new BadRequestException("Error input data " + filename));
        file.setFilename(newFilenameDto.getFilename());
        try{
            return fileRepository.save(file);
        } catch (Exception e){
            throw new InternalServerException("Server error, can not update file: " + filename);
        }
    }

    public File download(String token, String filename) {
        authorizationRepository.getUserEmail(token)
                .orElseThrow(()->new UnauthorizedException("Unauthorized error, please login again"));
        try {
            return fileRepository.findByFilename(filename)
                    .orElseThrow(()->new BadRequestException("Error input data " + filename));
        } catch (Exception e){
            throw new InternalServerException("Server error, can not download file: " + filename);
        }
    }
}
