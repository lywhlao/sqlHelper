package com.netease.activity.dao;

import com.netease.activity.annoation.TableName;
import com.netease.activity.sample.UserEntity;
import org.apache.ibatis.annotations.Param;

/**
 *
 * Created by hzlaojiaqi on 2016/10/18.
 */
@TableName("tb_yx_user_test")
public interface UserDaoTest {

    public int updateCharset();

    public  int insertUser(UserEntity userEntity);


    public UserEntity getUserById(long id);

    public UserEntity getUserByUid(String uid);

    public int updatePhoneByUid(@Param("uid") String uid,
                                @Param("phone") String phone);


    public int updatePhotoUrlByUid(@Param("uid") String uid,
                                   @Param("photoUrl") String photoUrl);


    public int updateBindingPhoneFlagByUid(@Param("uid") String uid,
                                           @Param("bindingPhone") boolean bindingPhone);

    public int updateGetPrizeFlagByUid(@Param("uid") String uid,
                                       @Param("getPrizeFlag") boolean getPrizeFlag);

    public int updatePhotoUrlFlagByUid(@Param("uid") String uid,
                                       @Param("PhotoUrlFlag") boolean PhotoUrlFlag);


    public int updateCompleteFlagByUid(@Param("uid") String uid,
                                       @Param("isComplete") boolean isComplete);


    public int decreasePlusNum(@Param("uid") String uid);



    public int decreaseMinusNum(@Param("uid") String uid);


    public int updateLasPrize(@Param("uid") String uid,
                              @Param("lastPrizeType") int lastPrizeType);


    public int updateCurrentPrize(@Param("uid") String uid,
                                  @Param("currentPrizeType") int currentPrizeType);


    public int updateFirstDifferentFlag(@Param("uid") String uid,
                                        @Param("isFirstDifferent") boolean isFirstDifferent);

    public int cleanTable();

    public int deleteUserByUid(String uid);

}
