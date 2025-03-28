package org.bengo.service.video.impl;

import org.bengo.entity.video.Type;
import org.bengo.mapper.video.TypeMapper;
import org.bengo.service.video.TypeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author wuGuanRen
 */
@Service
public class TypeServiceImpl extends ServiceImpl<TypeMapper, Type> implements TypeService {


    @Override
    public List<String> getLabels(Long typeId) {
        final List<String> labels = this.getById(typeId).buildLabel();
        return labels;
    }

    @Override
    public List<String> random10Labels() {
        final List<Type> types = list(null);
        Collections.shuffle(types);
        final ArrayList<String> labels = new ArrayList<>();
        for (Type type : types) {
            for (String label : type.buildLabel()) {
                if (labels.size() == 10){
                    return labels;
                }
                labels.add(label);
            }
        }
        return labels;
    }
}
