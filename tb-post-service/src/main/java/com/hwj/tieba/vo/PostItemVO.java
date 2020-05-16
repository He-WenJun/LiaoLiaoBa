package com.hwj.tieba.vo;

import com.hwj.tieba.entity.File;
import java.util.List;

public class PostItemVO {
    private PostVO postVO;
    private List<File> fileList;

    public PostVO getPostVO() {
        return postVO;
    }

    public void setPostVO(PostVO postVO) {
        this.postVO = postVO;
    }

    public List<File> getFileList() {
        return fileList;
    }

    public void setFileList(List<File> fileList) {
        this.fileList = fileList;
    }
}
