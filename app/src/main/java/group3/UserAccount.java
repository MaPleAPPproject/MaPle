package group3;

public class UserAccount {

        private String MemberId;
        private String Email;
        private String PassWord;
        private	String UserName;
        private	String ProfileIcon;
        private	String SelfIntroduction;
        private String VipStatus;

        public UserAccount(String Email, String PassWord) {
            this.MemberId=MemberId;
            this.Email=Email;
            this.PassWord=PassWord;
            this.UserName=UserName;
            this.ProfileIcon=ProfileIcon;
            this.SelfIntroduction=SelfIntroduction;
            this.VipStatus=VipStatus;
        }

        public String getMemberId() {
            return MemberId;
        }

        public void setMemberId(String MemberId) {
            this.MemberId = MemberId;
        }

        public String getEmail() {
            return Email;
        }

        public void setEmail(String Email) {
            this.Email = Email;
        }

        public String getPassWord() {
            return PassWord;
        }

        public void setPassWord(String PassWord) {
            this.PassWord = PassWord;
        }

        public String getUserName() {
            return UserName;
        }

        public void setUserName(String UserName) {
            this.UserName = UserName;
        }
        public String getProfileIcon() {
            return ProfileIcon;
        }

        public void setProfileIcon(String ProfileIcon) {
            this.ProfileIcon = ProfileIcon;
        }
        public String getSelfIntroduction() {
            return SelfIntroduction;
        }

        public void setUSelfIntroduction(String SelfIntroduction) {
            this.SelfIntroduction = SelfIntroduction;
        }
        public String getVipStatus() {
            return VipStatus;
        }

        public void setVipStatus(String VipStatus) {
            this.VipStatus = VipStatus;
        }
    }

