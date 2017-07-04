package com.netease.activity.sample;


import com.netease.activity.annoation.*;

/**
 * Created by hzlaojiaqi on 2016/10/17.
 */
@TableName("tb_yq_user")
@Desc("用户表")
public class UserEntity {

   @PrimaryKey
   long id;
   @UniqueKey("IDX_YQ_USER_UID")
   @Desc("账号")
   @PrimaryKey
   String uid;
   @UniqueKey("IDX_YQ_USER_UID")
   @Desc("设备号")
   String uuid;//刚开始取不到，变为有钱用户以后去得到
   String ip;
   @UniqueKey("IDX_YQ_USER_SECO")
   String mobile;
   @UniqueKey("IDX_YQ_USER_SECO")
   long amount;//基金金额  分
   boolean useRedPacket;//是否使用红包
   boolean isBinding; //是否绑定手机号
   boolean isBefore;  //是否在这之前注册
   boolean isWinner; //是否为中奖用户
   boolean isPrized; //是否领取奖品

   long insertTime;

   public boolean isPrized() {
      return isPrized;
   }

   public void setPrized(boolean prized) {
      isPrized = prized;
   }
   public boolean isWinner() {
      return isWinner;
   }

   public void setWinner(boolean winner) {
      isWinner = winner;
   }

   public long getAmount() {
      return amount;
   }

   public void setAmount(long amount) {
      this.amount = amount;
   }

   public boolean isUseRedPacket() {
      return useRedPacket;
   }

   public void setUseRedPacket(boolean useRedPacket) {
      this.useRedPacket = useRedPacket;
   }

   public String getMobile() {
      return mobile;
   }

   public void setMobile(String mobile) {
      this.mobile = mobile;
   }

   public long getId() {
      return id;
   }

   public void setId(long id) {
      this.id = id;
   }

   public String getUid() {
      return uid;
   }

   public void setUid(String uid) {
      this.uid = uid;
   }

   public String getUuid() {
      return uuid;
   }

   public void setUuid(String uuid) {
      this.uuid = uuid;
   }

   public String getIp() {
      return ip;
   }

   public void setIp(String ip) {
      this.ip = ip;
   }

   public boolean isBinding() {
      return isBinding;
   }

   public void setBinding(boolean binding) {
      isBinding = binding;
   }

   public boolean isBefore() {
      return isBefore;
   }

   public void setBefore(boolean before) {
      isBefore = before;
   }

   public long getInsertTime() {
      return insertTime;
   }

   public void setInsertTime(long insertTime) {
      this.insertTime = insertTime;
   }

   @Override
   public String toString() {
      return "UserEntity{" +
              "id=" + id +
              ", uid='" + uid + '\'' +
              ", uuid='" + uuid + '\'' +
              ", ip='" + ip + '\'' +
              ", mobile='" + mobile + '\'' +
              ", amount=" + amount +
              ", useRedPacket=" + useRedPacket +
              ", isBinding=" + isBinding +
              ", isBefore=" + isBefore +
              ", isWinner=" + isWinner +
              ", isPrized=" + isPrized +
              ", insertTime=" + insertTime +
              '}';
   }
}
