package rf.ivanpavlov.voicechanger.bot;

import org.apache.commons.io.FileUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;


public class FileAPI {
    // @Value("${url.api.convert}")
    String apiUrlConvert = "http://localhost:8000/convert";
    String apiUrlAddZip = "http://localhost:8000/add_zip";  // URL вашего API        // Путь к файлу

    public File convert(File file, Map<String, String> settings) throws Exception {
        DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("dd.MM.yy_HH-mm-ss");
        String dateTime = LocalDateTime.now().format(formatter1);

        RestTemplate restTemplate = new RestTemplate();
        // Создаем тело запроса
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new FileSystemResource(file));
        body.add("model", settings.get("model"));
        body.add("pitch", settings.get("pitch"));
        body.add("algorithm", settings.get("algorithm"));
        // Устанавливаем заголовки
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        // Отправляем запрос
        ResponseEntity<byte[]> response = restTemplate.postForEntity(apiUrlConvert, requestEntity, byte[].class);
        // Проверяем статус ответа
        if (response.getStatusCode() == HttpStatus.OK) {
            byte[] responseBody = response.getBody();
            // Сохраняем полученные данные в файл
            File outputFile = new File("tempFiles/" + settings.get("model") + "_" +
                    settings.get("pitch") + "_" + settings.get("algorithm") + "_" + dateTime + ".wav");
            if (responseBody == null) {
                throw new Exception("Файл от сервера потерян.");
            }
            FileUtils.writeByteArrayToFile(outputFile, responseBody);

            return outputFile;
        }
        System.out.println("Status Code: " + response.getStatusCode());
        return null;
    }

    public String add_zip(File file) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        // Создаем тело запроса
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file_path", file.getAbsolutePath());
        // Устанавливаем заголовки
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(apiUrlAddZip, requestEntity, String.class);
        String responseBody = response.getBody();
        if (responseBody == null) {
            throw new Exception("Ответ от сервера потерян.");
        }
        return responseBody;
    }
}
