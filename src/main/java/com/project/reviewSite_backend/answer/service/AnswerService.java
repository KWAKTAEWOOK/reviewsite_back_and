package com.project.reviewSite_backend.answer.service;

import com.amazonaws.services.s3.AmazonS3;
import com.project.reviewSite_backend.answer.dao.AnswerRepository;
import com.project.reviewSite_backend.answer.domain.Answer;
import com.project.reviewSite_backend.answer.dto.AnswerDto;
import com.project.reviewSite_backend.answer.dto.AnswerVo;
import com.project.reviewSite_backend.answer.dto.CreateAnswerForm;
import com.project.reviewSite_backend.answer.dto.StarcountDto;
import com.project.reviewSite_backend.photo.domain.Photo;
import com.project.reviewSite_backend.photo.dto.PhotoDto;
import com.project.reviewSite_backend.photo.service.PhotoService;
import com.project.reviewSite_backend.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnswerService {
    private final AnswerRepository answerRepository;

    private final PhotoService photoService;

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    //------------------------------------------------------------------------------------------------

    public Answer createAnswer(CreateAnswerForm createAnswereForm, User user) {
        Answer answer = Answer.builder()
                .content(createAnswereForm.getContent())
                .star(createAnswereForm.getStar())
                .createDate(LocalDateTime.now())
                .detail_name(createAnswereForm.getDetail_name())
                .detailId(createAnswereForm.getDetail_id())
                .nickname(createAnswereForm.getNickname())
                .user(user)
                .build();
        Answer answer1 = answerRepository.save(answer);
        return answer1;
    }


    //-----------------------------------------------------------------------------------
    //생성하는 로직
    public AnswerVo starin(AnswerVo answerVo, User user) {

        Answer d = new Answer();
        d.setStar(answerVo.getStar());
        d.setContent(answerVo.getContent());
        d.setCreateDate(LocalDateTime.now());
        d.setDetailId(answerVo.getDetail_id());
        d.setDetail_name(answerVo.getDetail_name());
        d.setNickname(answerVo.getNickname());
        d.setUser(user);
        Answer answer = answerRepository.save(d);
        AnswerVo answerVo1 = new AnswerVo(answer);

        return answerVo1;
    }
//---------------------------------------------------------------------------------------
public AnswerDto getAnswer(Long id) {
    Answer answer = answerRepository.findById(id).orElseThrow();
    AnswerDto answerDto = new AnswerDto(answer);
    return answerDto;
}
//--------------------------------------------------------------
public boolean updateContent(CreateAnswerForm createAnswerForm) {
    Optional<Answer> opContent = this.answerRepository.findById(createAnswerForm.getId());

    if (opContent.isPresent()) {
        Answer content = opContent.get();
        content.setId(createAnswerForm.getId());
        content.setContent(createAnswerForm.getContent());
        content.setStar(createAnswerForm.getStar());
        content.setCreateDate(LocalDateTime.now());
        answerRepository.save(content);
        return true;
    } else {
        return false;
    }

}

    public List<AnswerVo> answers(Long detailId) {
        return answerRepository.findByDetailId(detailId)
                .stream()
                .map(answer -> {
                    return new AnswerVo(answer);
                })
                .collect(Collectors.toList());
    }

    //-----------------------------------------------------------------------------------
    //id값으로 삭제하는 로직
//    public Answer deleteById(Long id) {
//        try {
//            answerRepository.deleteById(id);
//
//            return deleteById(id);
//        } catch (
//                EmptyResultDataAccessException e) {//오류 예외
//            return null;
//        }
//    }
    public void deleteArticle(Long id) {
        Answer answer = answerRepository.findById(id).orElseThrow();

        List<PhotoDto> photoList = photoService.getphotoimgByAnswer(answer);

        photoList
                .stream()
                .forEach(photoDto -> {
                    String imgUrl = photoDto.getImgUrl();
                    String filename = imgUrl.substring(imgUrl.lastIndexOf("/") + 1, imgUrl.length());
                    amazonS3.deleteObject(bucket, filename);

                });

        answerRepository.deleteById(id);

    }
    //------------------------------------------------------------------------------------
    //평점 평균구하는 로직
    public StarcountDto staravg(Long detailId) {
        StarcountDto starcountDto = new StarcountDto();
        List<Answer> getstar = answerRepository.findByDetailId(detailId);
        Long avg = 0L;
        for (Answer answer : getstar) {
            avg += answer.getStar();
        }
        starcountDto.setCount(getstar.size());
        starcountDto.setStar((double) avg / getstar.size());
        return starcountDto;
    }

    public List<AnswerVo> findCommentByUserId(User user) {
        if (user == null) {
            return null;
        }
        return answerRepository.findByUser(user);
    }
}




