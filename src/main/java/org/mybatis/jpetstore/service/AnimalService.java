package org.mybatis.jpetstore.service;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import net.sourceforge.stripes.action.FileBean;
import net.sourceforge.stripes.action.ForwardResolution;
import org.mybatis.jpetstore.configuration.AWSS3;
import org.mybatis.jpetstore.domain.AnimalMating;
import org.mybatis.jpetstore.mapper.AnimalMapper;
import org.mybatis.jpetstore.web.actions.AnimalActionBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

import static org.mybatis.jpetstore.configuration.Constants.BUCKET;


@Service
public class AnimalService {

    private final AnimalMapper animalMapper;

    public AnimalService(AnimalMapper animalMapper) {
        this.animalMapper = animalMapper;
    }



    public int insertAnimal(AnimalMating animalMating) {
        animalMapper.insertAnimal(animalMating);
        return animalMating.getId();
    }

    public int editAnimal(AnimalMating animalMating){
        animalMapper.editAnimal(animalMating);
        return animalMating.getId();
    }

    public void updateStatus(int id, String status) {
        Map<String, Object> condition = new HashMap<>();
        condition.put("id", id);
        condition.put("status", status);
        animalMapper.updateStatus(condition);
    }

    public String getMatingStatusValue(int id) {
        Map<String, Object> condition = new HashMap<>();
        condition.put("id", id);
        return animalMapper.getStatus(condition);
    }

    /*게시글 상세조회*/
    public AnimalMating getAnimalMattingDetail(int id, String userId) {
        return animalMapper.getAnimalMattingDetail(userId,id);
    }

    public List<String> getAnimalMatingCha(int id) {
        return animalMapper.getAnimalMatingCha(id);
    }



    public AWSS3 awsS3 = AWSS3.getInstance();

    private String bucketName=BUCKET;

    private FileBean fileBean;
    public void setFileBean(FileBean fileBean) {
        this.fileBean = fileBean;
    }

    public FileBean getFileBean() {
        return fileBean;
    }
    private Logger logger = LoggerFactory.getLogger(AnimalActionBean.class);

    public String uploadImgFile(FileBean fileBean) throws IOException {
        try {
            System.out.println(fileBean.getFileName());
            String fName = fileBean.getFileName();
            System.out.println(fName.indexOf("."));

            if (fName.indexOf(".") != -1) {
                String ext = fName.split("\\.")[1];
                String contentType="";
                switch (ext) {
                    case "jpeg":
                        contentType = "image/jpeg";
                        break;
                    case "png":
                        contentType = "image/png";
                        break;
                    case "txt":
                        contentType = "text/plain";
                        break;
                    case "csv":
                        contentType = "text/csv";
                        break;
                    case "jpg":
                        contentType = "image/jpg";
                        break;
                }

                ObjectMetadata metadata=new ObjectMetadata();
                metadata.setContentType(contentType);
                PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, UUID.randomUUID() + "." + ext, fileBean.getInputStream(),metadata);
                putObjectRequest.setCannedAcl(CannedAccessControlList.PublicRead);
                awsS3.uploadToS3(putObjectRequest);
                logger.info("===================== Upload File - Done! =====================");
                return "https://jpet-img.s3.ap-northeast-2.amazonaws.com/"+putObjectRequest.getKey();

            }
        } catch (AmazonServiceException ase) {
            logger.info("Caught an AmazonServiceException from PUT requests, rejected reasons:");
            logger.info("Error Message:    " + ase.getMessage());
            logger.info("HTTP Status Code: " + ase.getStatusCode());
            logger.info("AWS Error Code:   " + ase.getErrorCode());
            logger.info("Error Type:       " + ase.getErrorType());
            logger.info("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            logger.info("Caught an AmazonClientException: ");
            logger.info("Error Message: " + ace.getMessage());
        }
        return null;
    }

    public int getCount(String searchTag, String keyword) {
        Map<String, Object> condition = new HashMap<>();
        if(keyword == null)
            condition.put("value", "%%");
        else
            condition.put("value", "%" + keyword + "%");
        condition.put("searchTag", searchTag);


        return animalMapper.getAnimalMatingCount(condition);
    }

    public int getRecommendCount(String id, String searchTag, String keyword) {
        Map<String, Object> condition = new HashMap<>();
        condition.put("id", id);
        condition.put("searchTag", searchTag);
        if(keyword == null)
            condition.put("value", "%%");
        else
            condition.put("value", "%" + keyword + "%");

        return animalMapper.getRecommendAnimalMatingCount(condition);
    }

    public void plusViewCount(int id) {
        animalMapper.plusViewCount(id);
    }

    public void plusPreferView(String id, String character) {
        HashMap<String, Object> map = new HashMap();
        map.put("userId", id);
        map.put("character",character);
        animalMapper.plusPreferView(map);
    }

    //전체 및 검색 keyword 또는 searchTah가 null이면 알아서 전체로 검색
    public List<AnimalMating> searchAnimal(int start, int end, String searchTag, String keyword){
        Map<String, Object> condition = new HashMap<>();
        condition.put("searchTag", searchTag);
        condition.put("start", start - 1);
        condition.put("end", end);
        if(keyword == null)
            condition.put("value", "%%");
        else
            condition.put("value", "%" + keyword + "%");

        return animalMapper.getAnimalMating(condition);
    }

    //추천 및 추천 검색 keyword 또는 searchTah가 null이면 알아서 전체로 검색
    public List<AnimalMating> searchRecommendAnimal(String id, int start, int end, String searchTag, String keyword){
        Map<String, Object> condition = new HashMap<>();
        condition.put("id", id);
        condition.put("searchTag", searchTag);
        condition.put("start", start - 1);
        condition.put("end", end);
        if(keyword == null)
            condition.put("value", "%%");
        else
            condition.put("value", "%" + keyword + "%");

        return animalMapper.getRecommendAnimalMating(condition);
    }


    public void addCharacter(int id, List<String> animalCharacters) {
        Map<String,Object> animalCharacter = new HashMap<>();
        for (int i=0;i<animalCharacters.size();i++) {
            animalCharacter.put("id",id);
            animalCharacter.put("character",animalCharacters.get(i));
            animalMapper.addCharacter(animalCharacter);
        }
    }
    /* animalCharaters : 변경 성격
       deleteCharaters : 기존 성격 + 변경 성격*/
    public void editCharacter(int id, List<String> animalCharacters){
        Map<String,Object> animalCharacter = new HashMap<>();

        for (int i=0;i<animalCharacters.size();i++) {
            animalCharacter.put("id",id);
            animalCharacter.put("character",animalCharacters.get(i));
            System.out.println(animalCharacters.get(i));
            animalMapper.editCharacter(animalCharacter);
        }
    }

    public String getUserId(String postId) {
        return animalMapper.getUserIdByPostId(Integer.parseInt(postId));
    }


    public void deleteOldCharacter(int id, List<String> animalCharacters) {
        List<String> deleteCharacters = animalMapper.getCharacterList(id);

        if (animalCharacters.containsAll((deleteCharacters)) == false) {
            Collection<String> characters = new ArrayList(animalCharacters);
            deleteCharacters.removeAll(characters);


            Map<String, Object> deleteanimalCharacter = new HashMap<>();
            for (int i = 0; i < deleteCharacters.size(); i++) {
                deleteanimalCharacter.put("id", id);
                deleteanimalCharacter.put("character", deleteCharacters.get(i));
                animalMapper.deleteOldCharacter(deleteanimalCharacter);
            }
        }
    }
    public void userAnimalDelete(int id){
        animalMapper.userAnimalDelete(id);
    }

    public List<String> getCharacterList(int id){
        return animalMapper.getCharacterList(id);
    }
}
