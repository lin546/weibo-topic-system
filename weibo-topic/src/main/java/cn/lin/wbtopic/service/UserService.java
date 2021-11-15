package cn.lin.wbtopic.service;

import cn.lin.wbtopic.mapper.UserMapper;
import cn.lin.wbtopic.model.User;
import cn.lin.wbtopic.service.result.ServiceResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;

@Service
public class UserService {

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private UserMapper userMapper;

    public User getUser(Integer id){
        if (id == null){
            throw new IllegalArgumentException("入参id不能为空");
        }
        return userMapper.selectByPrimaryKey(id);
    }

    public ServiceResult login(String loginName,String password){
        ServiceResult serviceResult = new ServiceResult();
        User user = userMapper.selectByLoginName(loginName);
        if(user == null){
            serviceResult.setSuccess(false);
            serviceResult.setData("用户名或密码错误");
            return serviceResult;
        }
        String encrptPassword = DigestUtils.md5DigestAsHex(password.getBytes(StandardCharsets.UTF_8));
        if(!encrptPassword.equals(user.getPassword())){
            serviceResult.setSuccess(false);
            serviceResult.setData("用户名或密码错误");
            return serviceResult;
        }
        serviceResult.setSuccess(true);
        serviceResult.setData(user);
        return serviceResult;
    }

    /**
     * 用户注册服务
     * @param loginName 注册名称
     * @param password  注册密码
     */
    @Transactional
    public ServiceResult register(String loginName, String password, String nickName, Integer age, Integer gender, String avatarUrl){
        ServiceResult serviceResult = new ServiceResult();
        User user = new User();
        user.setLoginName(loginName);
        //使用MD5加密
        user.setPassword(DigestUtils.md5DigestAsHex(password.getBytes(StandardCharsets.UTF_8)));
        user.setNickName(nickName);
        user.setAge(age);
        user.setGender(gender.byteValue());
        user.setAvatarUrl(avatarUrl);

       try {
           userMapper.insertSelective(user);
       }catch (DuplicateKeyException duplicateKeyException){
            serviceResult.setSuccess(false);
            serviceResult.setData("用户登录名重复");
       }
       return serviceResult;
    }
}
