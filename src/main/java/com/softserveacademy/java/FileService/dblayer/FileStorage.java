package com.softserveacademy.java.FileService.dblayer;


public interface FileStorage {
    /**
     * Save file with the specified id into the storage
     *
     * @param id the file id
     * @param bytes file bytes
     */
    void save (String id, byte[] bytes);

    /**
     * Load file with the specified id from the storage
     *
     * @param id the file id
     * @return file bytes
     */
    byte [] load (String id);

    /**
     * Delete file with the specified id
     *
     * @param id the file id
     */
    void delete(String id);
}
