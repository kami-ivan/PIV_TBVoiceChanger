package rf.ivanpavlov.voicechanger.bot;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class FileAPI {

    public File convert(File file) {
        String apiUrlConvert = "http://localhost:8000/convert";  // URL вашего API        // Путь к файлу

        RestTemplate restTemplate = new RestTemplate();

        // Создаем тело запроса
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new FileSystemResource(file.getPath()));

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
            File outputFile = new File(file.getPath().replace("downloads", "file_to_send"));
            try (FileOutputStream out = new FileOutputStream(outputFile)) {
                if (responseBody != null) {
                    out.write(responseBody);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println("Аудиофайл успешно сохранен: " + outputFile.getAbsolutePath());
            return outputFile;
        }
        System.out.println("Status Code: " + response.getStatusCode());
        return null;
    }

    public String add_zip(File file) {

        String apiUrlAddZip = "http://localhost:8000/add_zip";

        RestTemplate restTemplate = new RestTemplate();

        // Создаем тело запроса
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file_path", file.getAbsolutePath());
        // Устанавливаем заголовки
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(apiUrlAddZip, requestEntity, String.class);


        System.out.println(response.getBody());
        return response.getBody();
    }
}
