package org.disk.service;

import org.disk.entity.UserFileList;

public interface AsyncService {

    /**
     * 修改名字
     * @param pathName 新名字
     * @param file 文件对象
     */
    void renameDirectory(String pathName, UserFileList file);
}
