package com.shiqu.wemedia.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shiqu.model.common.dtos.ResponseResult;
import com.shiqu.model.wemedia.dtos.WmMaterialDto;
import com.shiqu.model.wemedia.pojos.WmMaterial;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

public interface WmMaterialService extends IService<WmMaterial> {

    /**
     * 图片上传
     * @param multipartFile
     * @return
     */
    public ResponseResult uploadPicture(MultipartFile multipartFile);

    /**
     * 素材列表查询
     * @param dto
     * @return
     */
    public ResponseResult findList(WmMaterialDto dto);

    /**
     * 删除图片
     * @param id
     * @return
     */
    public ResponseResult delPicture(Integer id);

}