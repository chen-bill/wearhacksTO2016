package oldpeoplesavers.savetheoldpeople;

/**
 * Created by Tony on 16-03-27.
 */
public class Memo {

    String content;
    String title;
    Long timestamp;

    public Memo(String title, String content, Long timeStamp){
        this.title = title;
        this.content = content;
        this.timestamp = timeStamp;
    }

    public String getContent(){
        return content;
    }

    public String getTitle(){
        return title;
    }

    public Long getTimeStamp(){
        return timestamp;
    }



}
