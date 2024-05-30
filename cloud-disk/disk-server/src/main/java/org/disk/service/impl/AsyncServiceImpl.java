package org.disk.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.disk.entity.UserFileList;
import org.disk.mapper.UserFileListMapper;
import org.disk.service.AsyncService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;

@Service
public class AsyncServiceImpl implements AsyncService {

    @Resource
    UserFileListMapper userFileListMapper;

    @Override
    @Transactional
    @Async
    public void renameDirectory(String pathName,UserFileList file) {
        String old = file.getFileName();
        file.setFileName(pathName);
        file.setChangeDate(LocalDateTime.now());
        userFileListMapper.updateById(file);

        for (UserFileList files : userFileListMapper.selectList(new QueryWrapper<UserFileList>().lambda()
                .eq(UserFileList::getParentPath, old))) {
            if(Boolean.TRUE.equals(files.getIsDir())){
                String fileName = files.getFileName();
                if(fileName.startsWith(old)){
                    String newPath=fileName.replaceFirst(old,pathName);
                    renameDirectory(newPath,files);
                    files.setFileName(newPath);
                }
            }
            files.setParentPath(pathName);
            files.setChangeDate(LocalDateTime.now());
            userFileListMapper.updateById(files);
        }

    }


}
