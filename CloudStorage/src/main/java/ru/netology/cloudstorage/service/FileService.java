package ru.netology.cloudstorage.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class FileService {
    private final FileRepository fileRepository;
    private final UserService userService;
    private final AuthorizationRepositoryImpl authorizationRepository;

    public List<FileDto> getAllFiles(String token, int limit){
        log.info("Trying to get email by token");
        String email = authorizationRepository.getUserEmail(token)
                .orElseThrow( ()->new UnauthorizedException("Unauthorized error, please login again") );
        log.info("Email received. Trying to get User by email");
        User user = userService.loadUserByUsername(email);
        log.info("User received: " + user.getEmail() + ". Trying to get List of files by User");
        List<File> file = fileRepository.getFileByUserId(user.getId())
                .orElseThrow(()->new InternalServerException("Server error, can not get List of files"));
        log.info("List of file received for user: " + user.getEmail());
        return file.stream()
                .map(o -> new FileDto(o.getFilename(), o.getSize()))
                .collect(Collectors.toList());
    }

    public File save(String token, String filename, MultipartFile multipartFile) throws IOException {
        if (multipartFile == null || multipartFile.isEmpty()){
            log.error("Multipart file is null or empty. Filename: " + filename);
            throw new BadRequestException("Error input data, file cant be null or empty");
        }
        log.info("Trying to get email by token");
        String email = authorizationRepository.getUserEmail(token)
                .orElseThrow(()->new UnauthorizedException("Unauthorized error, please login again"));
        log.info("Email received. Trying to get User by email");
        User user = userService.loadUserByUsername(email);
        File file = File.builder()
                .filename(filename)
                .size(multipartFile.getSize())
                .fileData(multipartFile.getBytes())
                .user(user)
                .build();
        log.info("User received: " + user.getEmail() + ". Trying to save file in database.");
        return fileRepository.save(file);
    }

    public void delete(String token, String filename) {
        log.info("Trying to get email by token");
        authorizationRepository.getUserEmail(token)
                .orElseThrow(()->new UnauthorizedException("Unauthorized error, please login again"));
        log.info("Trying to get file by filename");
        File file = fileRepository.findByFilename(filename)
                .orElseThrow(()->new BadRequestException("Error input data " + filename));
        log.info("Trying to delete file from database");
        try {
            fileRepository.delete(file);
            log.info("file has been deleted: " + filename);
        } catch (Exception e){
            log.error("problem deleting file: " + filename);
            throw new InternalServerException("Server error, can not delete file: " + filename);
        }
    }

    public File update(String token, String filename, UpdateFileNameDto newFilenameDto) {
        log.info("Update file: " + filename + " is starting");
        log.info("Trying to get email by token");
        authorizationRepository.getUserEmail(token)
                .orElseThrow(()->new UnauthorizedException("Unauthorized error, please login again"));
        log.info("Trying to get file by filename");
        File file = fileRepository.findByFilename(filename)
                .orElseThrow(()->new BadRequestException("Error input data " + filename));
        file.setFilename(newFilenameDto.getFilename());
        log.info("Trying to update file: " + filename);
        try{
            return fileRepository.save(file);
        } catch (Exception e){
            log.error("problem updating file: " + filename);
            throw new InternalServerException("Server error, can not update file: " + filename);
        }
    }

    public File download(String token, String filename) {
        log.info("Download file: " + filename + " is starting");
        log.info("Trying to get email by token");
        authorizationRepository.getUserEmail(token)
                .orElseThrow(()->new UnauthorizedException("Unauthorized error, please login again"));
        try {
            log.info("Start to download file: " + filename);
            return fileRepository.findByFilename(filename)
                    .orElseThrow(()->new BadRequestException("Error input data " + filename));
        } catch (Exception e){
            log.error("problem with download file: " + filename);
            throw new InternalServerException("Server error, can not download file: " + filename);
        }
    }
}
