
package com.example.xch.scanzxing;


import java.util.ArrayList;
import java.util.List;


//数据结构文件

public class Questionnaire {
    public String id;
    public String pwd;
    public List<Question> questions = new ArrayList<Question>();
    public Response response = new Response();

    public class Question{
        public QuestionType type = QuestionType.multiple;
        public String question = "";
        public List<String> options = new ArrayList<String>();
    }
    public static class Response{
        public String ResponseID;
        public long TimeStamp;
        public long Latitude;
        public long Longitude;
        public String IMEI;
        public List<Answer> answer = new ArrayList<Answer>();
    }
    public static class Answer{
        public String AnswerKey;
        public int QuestionNum;
        public String content = "";
    }

    enum QuestionType{
        single,
        multiple,
        edit
    }
}



