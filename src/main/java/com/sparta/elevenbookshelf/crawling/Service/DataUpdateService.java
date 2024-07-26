package com.sparta.elevenbookshelf.crawling.Service;

import com.sparta.elevenbookshelf.crawling.CrawlingTest;
import com.sparta.elevenbookshelf.crawling.CrawlingTestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j(topic = "DataUpdateService")
public class DataUpdateService {

    private final CrawlingTestRepository crawlingTestRepository;

    @Value("${CSV_FILE_LOCATE}")
    private String csvFileLocate;

    public void updateDatabase() {
        Path csvFile = Paths.get(csvFileLocate);
        try (FileReader reader = new FileReader(csvFile.toFile());
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.builder()
                     .setHeader()
                     .setIgnoreHeaderCase(true)
                     .setTrim(true)
                     .build()))
        {
            for (CSVRecord csvRecord : csvParser) {
                Long id = Long.parseLong(csvRecord.get("Id"));
                String title = csvRecord.get("title");

                Optional<CrawlingTest> mainData = crawlingTestRepository.findById(id);

                if (mainData.isPresent()) {
                    updateData(mainData.get(), csvRecord);

                } else {
                    CrawlingTest newData = createNewData(csvRecord);
                    crawlingTestRepository.save(newData);
                }
            }

        } catch (IOException e) {
            log.error("CSV 파일 읽는중 오류 발생 : {}", e.getMessage());
        } catch (NumberFormatException e) {
            log.error("ID 파싱 중 오류 발생 : {}", e.getMessage());
        }

    }

    private void updateData(CrawlingTest existingData, CSVRecord csvRecord) {
        setCommonData(existingData, csvRecord);
    }

    private CrawlingTest createNewData(CSVRecord csvRecord) {
        CrawlingTest newData = new CrawlingTest();
        newData.setId(Long.parseLong(csvRecord.get("Id")));
        setCommonData(newData, csvRecord);
        return newData;
    }

    private void setCommonData(CrawlingTest data, CSVRecord csvRecord) {
        data.setPlatform(csvRecord.get("Platform"));
        data.setComicsOrBook(csvRecord.get("ComicsOrBook"));
        data.setContentType(csvRecord.get("ContentType"));
        data.setTitle(csvRecord.get("Title"));
        data.setAuthor(csvRecord.get("Author"));
        data.setCompleteOrNot(csvRecord.get("CompleteOrNot"));
        data.setRating(parseDoubleOrDefault(csvRecord.get("Rating")));
        data.setLikeCount(parseLongOrDefault(csvRecord.get("LikeCount")));
        data.setBookMark(parseLongOrDefault(csvRecord.get("BookMark")));
        data.setTotalView(parseDoubleOrDefault(csvRecord.get("TotalView")));
    }

    // Long 타입으로 파싱
    private Long parseLongOrDefault(String data) {

        if (data == null || data.trim().isEmpty()) {
            log.warn("Long 파싱중 입력받은 데이터가 비어있습니다. : {}", data);
            return 0L;
        }

        try {
            return Long.parseLong(data);
            } catch (NumberFormatException e) {
            log.error("Long 형태로 변환중 에러 발생 : {}", e.getMessage());
            return 0L;
        }

    }

    // Double 타입으로 파싱
    private Double parseDoubleOrDefault(String data) {

        if (data == null || data.trim().isEmpty()) {
            log.error("Double 파싱중 입력받은 데이터가 비어있습니다. : {}", data);
            return 0.0;
        }

        try {
            return Double.parseDouble(data);
        } catch (NumberFormatException e) {
            log.error("Double 형태로 변환중 에러 발생 : {}", e.getMessage());
            return 0.0;
        }

    }

}
