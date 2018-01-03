package com.softserveacademy.java.FileService.services;

import com.softserveacademy.java.FileService.dblayer.FileStorage;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author Nikita Mykhailov
 * @version 1.0
 * @since 2017-11-07
 **/
public interface FilesService {
    /**
     * This method receives MultipartFile and id from controller,
     * converts it to the Document and transmits it to FileStorage
     *
     * @param id   the id of the File
     * @param file the multipart file to p
     *
     * @see FileStorage
     */

    void saveFile(String id, MultipartFile file);

    /**
     * Receives bytes of the file from FileStorage by
     * a certain file id
     *
     * @param id the id of the file to receive
     * @return file bytes
     */
    byte[] getFile(String id);

    /**
     * This method receives unique identifier of file from controller,
     * and delete the file according to id
     *
     * @param id   the id of the File
     *
     * @see FileStorage
     */
    void deleteFile(String id);

}
