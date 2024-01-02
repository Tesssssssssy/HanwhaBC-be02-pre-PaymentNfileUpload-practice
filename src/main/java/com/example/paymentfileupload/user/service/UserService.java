package com.example.paymentfileupload.user.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.example.paymentfileupload.user.model.User;
import com.example.paymentfileupload.user.model.request.PostCreateUserReq;
import com.example.paymentfileupload.user.model.response.PostCreateUserRes;
import com.example.paymentfileupload.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
public class UserService {
    @Value("${project.upload.path}")
    private String uploadPath;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final Logger log = LoggerFactory.getLogger(UserService.class);
    private UserRepository userRepository;
    private AmazonS3 s3;
    // 다형성 이용 (AmazonS3는 interface이니까)


    public UserService(UserRepository userRepository, AmazonS3 s3) {
        this.userRepository = userRepository;
        this.s3 = s3;
    }

    /**
     *  아래 saveFile 메소드는 uploadService 등으로 분리해서 개발하는 것이 좋다.
     *
     *  현재 시간을 받아와서 날짜별(연/월/일)로 폴더를 만들어서 저장하도록 구현한다.
     *  파일 경로를 구분하는 건 linux와 window가 다르다.
     *  그래서 경로 구분자가 / 였는데 우리는 linux 서버에 배포할 거니까
     *  경로 구분자를 // 로 변경한다.
     *
     *  그리고 .yml 파일에 경로를 설정해두고 @Value로 변수에 저장해서 사용한다.
     *  project:
     *      upload:
     *          path: c:\test
     *
     *  그리고 uploadPath에 경로 구분자 등과 함께 upload 경로 설정
     *
     *  그리고 폴더를 만드는 코드도 추가해주어야 함.
     */
    public String makeFolder(){
        String str = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String folderPath = str.replace("/", File.separator);

        File uploadPathFolder = new File(uploadPath, folderPath);
        if(uploadPathFolder.exists() == false) {
            uploadPathFolder.mkdirs();
        }

        return folderPath;
    }

    public String saveFile(MultipartFile file) {
        String originalName = file.getOriginalFilename();
        String folderPath = makeFolder();

        String uuid = UUID.randomUUID().toString();
        // 절대 중복되지 않는 random UUID값 생성

        String saveFileName = folderPath+ File.separator + uuid + "_" + originalName;
        // 생성된 uuid를 fileName에 추가

        // File saveFile = new File(uploadPath, saveFileName);
        /*
            uploadPath 아래 saveFileName으로 저장
            이 코드도 내 local pc에 저장할 떄 사용하는 코드이므로
            aws s3에 저장할 때는 사용 x
        */

        try {
            // file.transferTo(saveFile);  // 내 하드디스크에 file을 저장하는 코드

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(file.getContentType());

            // putObject가 upload
            s3.putObject(bucket, saveFileName.replace(File.separator, "/"), file.getInputStream(), metadata);
            // AWS S3에 저장
            /*
                한 번 inputstream으로 읽으면 끝난 것
                그 다음에 또 s3에 저장하려고 하니까 없는 것을 읽으려고 하는 것.
                그래서 file.transferTo(saveFile)를 주석처리하면 에러 발생 x

                inputstream 사용 시 주의할 것.
                ex.)
                그림 파일 크기 바꿔서 저장할 수 있게 해달라.
                그럼 metaDATA 가져와서 가로, 세로 길이 가져와서
                알아서 바꾸게 해달라고 하거나 이런 여러 처리를 해줘야 함.

                inputstream, outputstream 공부하자!!
            */

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // return saveFileName;
        return s3.getUrl(bucket, saveFileName.replace(File.separator, "/")).toString();
    }

    public PostCreateUserRes createUser(PostCreateUserReq postCreateUserReq) {
        String saveFileName = saveFile(postCreateUserReq.getImage());

        User user = User.builder()
                .email(postCreateUserReq.getEmail())
                .password(postCreateUserReq.getPassword())
                .name(postCreateUserReq.getName())
                .image(saveFileName.replace(File.separator, "/"))
                .build();

        User result = userRepository.save(user);

        PostCreateUserRes response = PostCreateUserRes.builder()
                .id(result.getId())
                .email(result.getEmail())
                .name(result.getName())
                .image(result.getImage())
                .build();

        return response;
    }

    public void findUser(Long id) {
        userRepository.findById(id);
    }
}
