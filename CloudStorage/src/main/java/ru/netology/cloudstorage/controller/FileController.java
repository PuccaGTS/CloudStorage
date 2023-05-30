package ru.netology.cloudstorage.controller;

import lombok.AllArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.cloudstorage.dto.file.FileDto;
import ru.netology.cloudstorage.dto.file.UpdateFileNameDto;
import ru.netology.cloudstorage.model.File;
import ru.netology.cloudstorage.service.FileService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/")
@AllArgsConstructor
public class FileController {
    private FileService fileService;

    @GetMapping("list")
    public ResponseEntity<List<FileDto>> getAllFiles(
            @RequestHeader("auth-token") String token,
            @RequestParam int limit
    ) {
        List<FileDto> files = fileService.getAllFiles(token, limit);
        return new ResponseEntity<>(files, HttpStatus.OK);
    }
    @GetMapping("file")
    public ResponseEntity<?> downloadFile(
            @RequestHeader("auth-token") String token,
            @RequestParam("filename") String filename
    ){
        File file = fileService.download(token, filename);
        ByteArrayResource resource = new ByteArrayResource(file.getFileData());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getFilename())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(file.getSize())
                .body(resource);
    }

    @PostMapping(value = "file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadFile(
            @RequestHeader("auth-token") String token,
            @RequestParam("filename") String filename,
            @RequestPart MultipartFile file
    ) throws IOException {
        fileService.save(token, filename, file);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("file")
    public ResponseEntity<?> deleteFile(
            @RequestHeader("auth-token") String token,
            @RequestParam("filename") String filename
    ){
        fileService.delete(token, filename);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("file")
    public ResponseEntity<?> updateFilename(
            @RequestHeader("auth-token") String token,
            @RequestParam("filename") String filename,
            @RequestBody UpdateFileNameDto newFilenameDto
    ){
        fileService.update(token, filename, newFilenameDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
