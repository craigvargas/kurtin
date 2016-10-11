package com.travelguide.models;

import com.parse.ParseFile;

import static java.lang.System.currentTimeMillis;

/**
 * Created by cvar on 10/10/16.
 */

//Competitor is currently not a Parse class
//It is used to combine necessary leaderboard data from two seperate tables: Leaderboard and User

public class Competitor {

    String userId;
    String name;
    Integer points;
    ParseFile parseFilePic;
    Integer rank;
    long objId;

    public Competitor() {
        userId = null;
        name = null;
        parseFilePic = null;
        points = -1;
        rank = -1;
        objId = currentTimeMillis();
    }

    public Competitor(String userIdArg, String nameArg, int pointsArg, ParseFile parseFilePicArg, int rankArg) {
        userId = userIdArg;
        name = nameArg;
        points = pointsArg;
        parseFilePic = parseFilePicArg;
        rank = rankArg;
        objId = currentTimeMillis();
    }

    public String getUserId() {
        return userId;
    }

    public ParseFile getParseFilePic() {
        return parseFilePic;
    }

    public String getName() {
        return name;
    }

    public Integer getPoints() {
        return points;
    }

    public Integer getRank() {
        return rank;
    }

    public long getObjId() {
        return objId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public void setParseFilePic(ParseFile parseFilePic) {
        this.parseFilePic = parseFilePic;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }
}
