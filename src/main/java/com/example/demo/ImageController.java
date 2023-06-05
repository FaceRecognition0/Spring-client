package com.example.demo;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Controller
public class ImageController {

    private final S3Service s3Service;

    //메인 페이지
    @GetMapping("/")
    public String home(){
        return "app";
    }


    @PostMapping("/upload")
    public String uploadImages(@RequestParam("file") final MultipartFile[] files, Model model) throws IOException {

        JsonNode response; //응답받을 객체
        List<String> imageUrls = new ArrayList<>(); //이미지 주소
        LinkedMultiValueMap<String, Object> imgMap = new LinkedMultiValueMap<>();
        RestTemplate restTemplate = new RestTemplate();

        // 1.이미지를 LinkedMultiValueMap 에 담는다.(Body)
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                imgMap.add("file", new MultipartInputStreamFileResource(file.getInputStream(), file.getOriginalFilename()));
            }
        }

        /*Flask 에 요청 보내기*/
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA); //2. Header 에 데이터 타입 명시

        String url = "http://localhost:5000/multiFileUploads"; //요청할 주소

        //3. HttpEntity 에 Body 와 Header 담기
        HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = new HttpEntity<>(imgMap, headers);
        //4. 서버에 요청
        response = restTemplate.postForObject(url, requestEntity, JsonNode.class);

        //Json을 Map으로 변경
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> result = mapper.convertValue(response, new TypeReference<Map<String, Object>>(){});

        List<Object> results = new ArrayList<>();

        //Map의 value를 List에 저장
        Iterator<Map.Entry<String,Object>> it = result.entrySet().iterator();
        while(it.hasNext()) {
            Map.Entry<String,Object> entrySet = (Map.Entry<String,Object>) it.next();
            results.add(entrySet.getValue());
        }

        //S3 이미지 넣기
        for (MultipartFile file2 : files) {
            String imgUrl = s3Service.upload(file2);
            imageUrls.add(imgUrl);
        }
        imageUrls.remove(0); //본인 사진 빼기

        List<Result> finalResults = new ArrayList<>();

        //imgUrl과 닮은꼴 값을 ResultList에 넣어서 반환한다.
        for (int i=0; i< imageUrls.size(); i++){
            Result finalResult = new Result();
            finalResult.setValues(results.get(i));
            finalResult.setImgUrls(imageUrls.get(i));
            finalResults.add(finalResult);
        }

        //View로 값 전달
        model.addAttribute("results", finalResults);

        return "app";
    }

    class MultipartInputStreamFileResource extends InputStreamResource {

        private final String filename;

        MultipartInputStreamFileResource(InputStream inputStream, String filename) {
            super(inputStream);
            this.filename = filename;
        }

        @Override
        public String getFilename() {
            return this.filename;
        }

        @Override
        public long contentLength() throws IOException {
            return -1; // we do not want to generally read the whole stream into memory ...
        }
    }
}
