package io.rong.messages;
import io.rong.util.GsonUtil;

import java.util.List;

public class GroupMessageData {

    private String operatorNickname;
    private List<String> targetUserIds;
    private List<String> targetUserDisplayNames;
    private String timestamp;


    public GroupMessageData(){

    }
    public String getOperatorNickname() {
        return operatorNickname;
    }

    public void setOperatorNickname(String operatorNickname) {
        this.operatorNickname = operatorNickname;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public List<String> getTargetUserIds() {
        return targetUserIds;
    }

    public void setTargetUserIds(List<String> targetUserIds) {
        this.targetUserIds = targetUserIds;
    }

    public List<String> getTargetUserDisplayNames() {
        return targetUserDisplayNames;
    }

    public void setTargetUserDisplayNames(List<String> targetUserDisplayNames) {
        this.targetUserDisplayNames = targetUserDisplayNames;
    }
    @Override
    public String toString() {
        return GsonUtil.toJson(this, GroupMessageData.class);
    }
}
