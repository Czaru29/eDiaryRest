package com.ediary.DTO;

import com.ediary.domain.Message;
import lombok.*;

import javax.validation.constraints.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageDto {

    private Long id;

    @Size(min = 2, max = 20)
    private String title;

    @Size(max = 255)
    private String content;
    private Date date;

    private Message.Status status;

    //Senders
    private Long sendersId;
    private String sendersName;

    //Readers
    private List<String> readersName;
    @Size(min = 1)
    private List<Long> readersId;

    public String getSimpleDateFormat() {
        SimpleDateFormat SDformat = new SimpleDateFormat("dd-MM-yyy HH:mm");
        try {
            return SDformat.format(date);
        }catch (Exception e) {
            return "";
        }

    }
}
