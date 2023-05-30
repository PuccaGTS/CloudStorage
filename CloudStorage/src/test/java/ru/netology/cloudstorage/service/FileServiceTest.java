package ru.netology.cloudstorage.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.cloudstorage.dto.file.FileDto;
import ru.netology.cloudstorage.dto.file.UpdateFileNameDto;
import ru.netology.cloudstorage.exception.BadRequestException;
import ru.netology.cloudstorage.exception.InternalServerException;
import ru.netology.cloudstorage.exception.UnauthorizedException;
import ru.netology.cloudstorage.model.File;
import ru.netology.cloudstorage.model.Role;
import ru.netology.cloudstorage.model.User;
import ru.netology.cloudstorage.repository.FileRepository;
import ru.netology.cloudstorage.repository.impl.AuthorizationRepositoryImpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FileServiceTest {

    @Mock
    private FileRepository fileRepository;

    @Mock
    private UserService userService;

    @Mock
    private AuthorizationRepositoryImpl authorizationRepository;

    @InjectMocks
    private FileService fileService;

    private final static String VALID_TOKEN = "validToken";
    private final static String INVALID_TOKEN = "invalidToken";
    private final static String TEST_EMAIL = "test@example.com";
    private final static String TEST_FILENAME = "file.txt";
    @Test
    public void getAllFilesSuccess() {
        int limit = 10;
        User user = getTestUser();
        when(authorizationRepository.getUserEmail(VALID_TOKEN)).thenReturn(Optional.of(TEST_EMAIL));
        when(userService.loadUserByUsername(TEST_EMAIL)).thenReturn(user);

        List<File> files = new ArrayList<>();
        files.add(new File(1L, "file1.txt", 100L, new byte[0], user));
        files.add(new File(2L,"file2.txt", 200L, new byte[0], user));
        when(fileRepository.getFileByUserId(user.getId())).thenReturn(Optional.of(files));

        List<FileDto> result = fileService.getAllFiles(VALID_TOKEN, limit);

        assertEquals(2, result.size());
        assertEquals("file1.txt", result.get(0).getFilename());
        assertEquals(100L, result.get(0).getSize());
        assertEquals("file2.txt", result.get(1).getFilename());
        assertEquals(200L, result.get(1).getSize());

        verify(authorizationRepository).getUserEmail(VALID_TOKEN);
        verify(userService).loadUserByUsername(TEST_EMAIL);
        verify(fileRepository).getFileByUserId(user.getId());
    }

    @Test
    public void getAllFilesUnauthorizedException() {
        when(authorizationRepository.getUserEmail(INVALID_TOKEN)).thenReturn(Optional.empty());

        assertThrows(UnauthorizedException.class, () -> fileService.getAllFiles(INVALID_TOKEN, 10));

        verify(authorizationRepository).getUserEmail(INVALID_TOKEN);
        verifyNoInteractions(userService);
        verifyNoInteractions(fileRepository);
    }
    @Test
    public void getAllFilesInternalServerException() {
        int limit = 10;
        User user = getTestUser();
        when(authorizationRepository.getUserEmail(VALID_TOKEN)).thenReturn(Optional.of(TEST_EMAIL));
        when(userService.loadUserByUsername(TEST_EMAIL)).thenReturn(user);
        when(fileRepository.getFileByUserId(user.getId())).thenReturn(Optional.empty());

        assertThrows(InternalServerException.class, () -> fileService.getAllFiles(VALID_TOKEN, limit));

        verify(authorizationRepository).getUserEmail(VALID_TOKEN);
        verify(userService).loadUserByUsername(TEST_EMAIL);
        verify(fileRepository).getFileByUserId(user.getId());
    }

    @Test
    public void saveSuccess() throws IOException {
        File file = getTestFile();
        MultipartFile multipartFile = new MockMultipartFile(file.getFilename(), file.getFileData());

        when(authorizationRepository.getUserEmail(VALID_TOKEN)).thenReturn(Optional.of(TEST_EMAIL));

        when(fileRepository.save(any(File.class))).thenReturn(file);

        File result = fileService.save(VALID_TOKEN, file.getFilename(), multipartFile);

        assertNotNull(result);
        assertEquals(file.getFilename(), result.getFilename());
        assertEquals(file.getFileData().length, result.getSize());
        assertArrayEquals(file.getFileData(), result.getFileData());

        verify(authorizationRepository).getUserEmail(VALID_TOKEN);
        verify(userService).loadUserByUsername(TEST_EMAIL);
        verify(fileRepository).save(any(File.class));
    }

    @Test
    public void saveBadRequestException() {
        assertThrows(BadRequestException.class, () -> fileService.save(VALID_TOKEN, TEST_FILENAME, null));

        verifyNoInteractions(authorizationRepository);
        verifyNoInteractions(userService);
        verifyNoInteractions(fileRepository);
    }

    @Test
    public void updateSuccess() {
        UpdateFileNameDto newFilenameDto = new UpdateFileNameDto("new_file.txt");
        when(authorizationRepository.getUserEmail(VALID_TOKEN)).thenReturn(Optional.of(TEST_EMAIL));
        User user = getTestUser();
        File file = getTestFile();
        when(fileRepository.findByFilename(file.getFilename())).thenReturn(Optional.of(file));
        File updatedFile = new File(1L, newFilenameDto.getFilename(), 100L, new byte[0], user);
        when(fileRepository.save(file)).thenReturn(updatedFile);
        File result = fileService.update(VALID_TOKEN, file.getFilename(), newFilenameDto);

        assertNotNull(result);
        assertEquals(newFilenameDto.getFilename(), result.getFilename());
        assertEquals(100L, result.getSize());
        assertArrayEquals(new byte[0], result.getFileData());
        assertEquals(user, result.getUser());

        verify(authorizationRepository).getUserEmail(VALID_TOKEN);
        verify(fileRepository).save(file);
    }

    @Test
    public void updateUnauthorizedException() {
        UpdateFileNameDto newFilenameDto = new UpdateFileNameDto("new_file.txt");
        when(authorizationRepository.getUserEmail(VALID_TOKEN)).thenReturn(Optional.empty());

        assertThrows(UnauthorizedException.class, () -> fileService.update(VALID_TOKEN, TEST_FILENAME, newFilenameDto));

        verify(authorizationRepository).getUserEmail(VALID_TOKEN);
        verifyNoInteractions(userService);
        verifyNoInteractions(fileRepository);
    }

    @Test
    public void updateBadRequestException() {
        UpdateFileNameDto newFilenameDto = new UpdateFileNameDto("new_file.txt");
        when(authorizationRepository.getUserEmail(VALID_TOKEN)).thenReturn(Optional.of(TEST_EMAIL));
        when(fileRepository.findByFilename(TEST_FILENAME)).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class, () -> fileService.update(VALID_TOKEN, TEST_FILENAME, newFilenameDto));

        verify(authorizationRepository).getUserEmail(VALID_TOKEN);
        verify(fileRepository).findByFilename(TEST_FILENAME);
        verifyNoMoreInteractions(fileRepository);
    }

    @Test
    public void deleteSuccess() {
        when(authorizationRepository.getUserEmail(VALID_TOKEN)).thenReturn(Optional.of(TEST_EMAIL));

        File file = getTestFile();
        when(fileRepository.findByFilename(file.getFilename())).thenReturn(Optional.of(file));

        fileService.delete(VALID_TOKEN, file.getFilename());

        verify(authorizationRepository).getUserEmail(VALID_TOKEN);
        verify(fileRepository).findByFilename(file.getFilename());
        verify(fileRepository).delete(file);
    }

    @Test
    public void deleteUnauthorizedException() {
        when(authorizationRepository.getUserEmail(VALID_TOKEN)).thenReturn(Optional.empty());

        assertThrows(UnauthorizedException.class, () -> fileService.delete(VALID_TOKEN, TEST_FILENAME));

        verify(authorizationRepository).getUserEmail(VALID_TOKEN);
        verifyNoInteractions(fileRepository);
    }

    @Test
    public void deleteBadRequestException() {
        when(authorizationRepository.getUserEmail(VALID_TOKEN)).thenReturn(Optional.of(TEST_EMAIL));

        when(fileRepository.findByFilename(TEST_FILENAME)).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class, () -> fileService.delete(VALID_TOKEN, TEST_FILENAME));

        verify(authorizationRepository).getUserEmail(VALID_TOKEN);
        verify(fileRepository).findByFilename(TEST_FILENAME);
        verifyNoMoreInteractions(fileRepository);
    }

    @Test
    public void downloadSuccess() {
        when(authorizationRepository.getUserEmail(VALID_TOKEN)).thenReturn(Optional.of(TEST_EMAIL));
        File file = getTestFile();
        when(fileRepository.findByFilename(file.getFilename())).thenReturn(Optional.of(file));
        File result = fileService.download(VALID_TOKEN, file.getFilename());

        assertNotNull(result);
        assertEquals(file.getFilename(), result.getFilename());
        assertEquals(100L, result.getSize());
        assertArrayEquals(new byte[100], result.getFileData());
        assertNull(result.getUser());

        verify(authorizationRepository).getUserEmail(VALID_TOKEN);
        verify(fileRepository).findByFilename(file.getFilename());
    }

    @Test
    public void downloadUnauthorizedException() {
        when(authorizationRepository.getUserEmail(INVALID_TOKEN)).thenReturn(Optional.empty());
        assertThrows(UnauthorizedException.class, () -> fileService.download(INVALID_TOKEN, TEST_FILENAME));
        verify(authorizationRepository).getUserEmail(INVALID_TOKEN);
        verifyNoInteractions(fileRepository);
    }

    @Test
    public void downloadInternalServerException() {
        when(authorizationRepository.getUserEmail(VALID_TOKEN)).thenReturn(Optional.of(TEST_EMAIL));

        when(fileRepository.findByFilename(TEST_FILENAME)).thenReturn(Optional.empty());

        assertThrows(InternalServerException.class, () -> fileService.download(VALID_TOKEN, TEST_FILENAME));

        verify(authorizationRepository).getUserEmail(VALID_TOKEN);
        verify(fileRepository).findByFilename(TEST_FILENAME);
        verifyNoMoreInteractions(fileRepository);
    }

    private File getTestFile(){
        return File.builder()
                .id(1L)
                .filename("file.txt")
                .size(100L)
                .fileData(new byte[100])
                .user(null)
                .build();
    }

    private User getTestUser(){
        return User.builder()
                .id(1L)
                .email("test@test.com")
                .password("pass")
                .role(Role.USER)
                .build();
    }
}