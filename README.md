## 클라이언트(Spring) 로직
- View Layer에서 프로필 사진 한 장과 다른 사진 여러장을 첨부해 제출한다.&nbsp;
<img src = "https://github.com/FaceRecognition0/Spring-client/assets/95980876/9b2f41f0-f65c-45cb-ad33-63bd558e0db0" width="24%" height="24%">
 
- Controller에서 View로부터 받은 사진을 **S3**에 저장하고 **Flask 서버**에 전송하여 **리턴값**을 받는다.
  
  
- **리턴값**을 다시 **thymeleaf**를 활용하여 View Layer로 전송한다.
 
  <img src = "https://github.com/FaceRecognition0/Spring-client/assets/95980876/4c0e8873-1461-465f-a13d-5bb9b4f3058e" width="90%" height="90%">
