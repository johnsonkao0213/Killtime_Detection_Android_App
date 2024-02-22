package labelingStudy.nctu.boredom_detection.model;

import com.google.gson.Gson;


import java.util.LinkedHashMap;

//Singleton Answers ........

public class Answers {
    private volatile static Answers uniqueInstance;
    private volatile static Answers stateInstance;
    private volatile static Answers intentionInstance;
    private final LinkedHashMap<String, String> answered_hashmap = new LinkedHashMap<>();


    private Answers() {
    }

    public void put_answer(String key, String value) {
        answered_hashmap.put(key, value);
    }
    public void reset_answer(){
        answered_hashmap.clear(); // Clear the hashmap
    }
    public String get_json_object() {
        Gson gson = new Gson();
        return gson.toJson(answered_hashmap,LinkedHashMap.class);
    }

    @Override
    public String toString() {
        return String.valueOf(answered_hashmap);
    }


    public static Answers getInstance() {
        if (uniqueInstance == null) {
            synchronized (Answers.class) {
                if (uniqueInstance == null) {
                    uniqueInstance = new Answers();
                }
            }
        }
        return uniqueInstance;
    }

    public static Answers getStateInstance() {
        if (stateInstance == null) {
            synchronized (Answers.class) {
                if (stateInstance == null) {
                    stateInstance = new Answers();
                }
            }
        }
        return stateInstance;
    }

    public static Answers getIntentionInstance() {
        if (intentionInstance == null) {
            synchronized (Answers.class) {
                if (intentionInstance == null) {
                    intentionInstance = new Answers();
                }
            }
        }
        return intentionInstance;
    }
}
