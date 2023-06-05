## 클라이언트(Spring) 로직
- View Layer에서 프로필 사진 한 장과 다른 사진 여러장을 첨부해 제출한다.
&nbsp<img src = "https://github.com/FaceRecognition0/Spring-client/assets/95980876/9b2f41f0-f65c-45cb-ad33-63bd558e0db0" width="30%" height="30%">
 
- Controller에서 View로부터 받은 사진을 Flask 서버에 전송하여 리턴값을 받고 사진은 S3에 저장한다.
  
- 리턴값을 다시 thymeleaf를 활용하여 View Layer로 보낸다.
