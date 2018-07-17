package com.amazonaws.models.nosql;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

@DynamoDBTable(tableName = "humblehome-mobilehub-2128789566-Breakers")

public class BreakersDO {
    private String _userId;
    private Double _breakerId;
    private String _description;
    private String _label;
    private Double _state;

    @DynamoDBHashKey(attributeName = "userId")
    @DynamoDBIndexHashKey(attributeName = "userId", globalSecondaryIndexName = "userId-breakerId")
    public String getUserId() {
        return _userId;
    }

    public void setUserId(final String _userId) {
        this._userId = _userId;
    }
    @DynamoDBRangeKey(attributeName = "breakerId")
    @DynamoDBIndexRangeKey(attributeName = "breakerId", globalSecondaryIndexName = "userId-breakerId")
    public Double getBreakerId() {
        return _breakerId;
    }

    public void setBreakerId(final Double _breakerId) {
        this._breakerId = _breakerId;
    }
    @DynamoDBAttribute(attributeName = "description")
    public String getDescription() {
        return _description;
    }

    public void setDescription(final String _description) {
        this._description = _description;
    }
    @DynamoDBAttribute(attributeName = "label")
    public String getLabel() {
        return _label;
    }

    public void setLabel(final String _label) {
        this._label = _label;
    }
    @DynamoDBAttribute(attributeName = "state")
    public Double getState() {
        return _state;
    }

    public void setState(final Double _state) {
        this._state = _state;
    }

    public String toString() {
        return "userId : " + _userId + "\n" +
                "breakerId : " + _breakerId + "\n" +
                "label : " + _label + "\n" +
                "description : " + _description + "\n" +
                "state : " + _state;
    }

}
