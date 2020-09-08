package com.aero.ops.entity.po;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class DevicePOExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public DevicePOExample() {
        oredCriteria = new ArrayList<Criteria>();
    }

    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    public String getOrderByClause() {
        return orderByClause;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    protected abstract static class GeneratedCriteria {
        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<Criterion>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            return criteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
        }

        public Criteria andIdIsNull() {
            addCriterion("ID is null");
            return (Criteria) this;
        }

        public Criteria andIdIsNotNull() {
            addCriterion("ID is not null");
            return (Criteria) this;
        }

        public Criteria andIdEqualTo(Integer value) {
            addCriterion("ID =", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotEqualTo(Integer value) {
            addCriterion("ID <>", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThan(Integer value) {
            addCriterion("ID >", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("ID >=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThan(Integer value) {
            addCriterion("ID <", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThanOrEqualTo(Integer value) {
            addCriterion("ID <=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdIn(List<Integer> values) {
            addCriterion("ID in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotIn(List<Integer> values) {
            addCriterion("ID not in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdBetween(Integer value1, Integer value2) {
            addCriterion("ID between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotBetween(Integer value1, Integer value2) {
            addCriterion("ID not between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andUuidIsNull() {
            addCriterion("UUID is null");
            return (Criteria) this;
        }

        public Criteria andUuidIsNotNull() {
            addCriterion("UUID is not null");
            return (Criteria) this;
        }

        public Criteria andUuidEqualTo(String value) {
            addCriterion("UUID =", value, "uuid");
            return (Criteria) this;
        }

        public Criteria andUuidNotEqualTo(String value) {
            addCriterion("UUID <>", value, "uuid");
            return (Criteria) this;
        }

        public Criteria andUuidGreaterThan(String value) {
            addCriterion("UUID >", value, "uuid");
            return (Criteria) this;
        }

        public Criteria andUuidGreaterThanOrEqualTo(String value) {
            addCriterion("UUID >=", value, "uuid");
            return (Criteria) this;
        }

        public Criteria andUuidLessThan(String value) {
            addCriterion("UUID <", value, "uuid");
            return (Criteria) this;
        }

        public Criteria andUuidLessThanOrEqualTo(String value) {
            addCriterion("UUID <=", value, "uuid");
            return (Criteria) this;
        }

        public Criteria andUuidLike(String value) {
            addCriterion("UUID like", value, "uuid");
            return (Criteria) this;
        }

        public Criteria andUuidNotLike(String value) {
            addCriterion("UUID not like", value, "uuid");
            return (Criteria) this;
        }

        public Criteria andUuidIn(List<String> values) {
            addCriterion("UUID in", values, "uuid");
            return (Criteria) this;
        }

        public Criteria andUuidNotIn(List<String> values) {
            addCriterion("UUID not in", values, "uuid");
            return (Criteria) this;
        }

        public Criteria andUuidBetween(String value1, String value2) {
            addCriterion("UUID between", value1, value2, "uuid");
            return (Criteria) this;
        }

        public Criteria andUuidNotBetween(String value1, String value2) {
            addCriterion("UUID not between", value1, value2, "uuid");
            return (Criteria) this;
        }

        public Criteria andSensorTypeIsNull() {
            addCriterion("SENSOR_TYPE is null");
            return (Criteria) this;
        }

        public Criteria andSensorTypeIsNotNull() {
            addCriterion("SENSOR_TYPE is not null");
            return (Criteria) this;
        }

        public Criteria andSensorTypeEqualTo(String value) {
            addCriterion("SENSOR_TYPE =", value, "sensorType");
            return (Criteria) this;
        }

        public Criteria andSensorTypeNotEqualTo(String value) {
            addCriterion("SENSOR_TYPE <>", value, "sensorType");
            return (Criteria) this;
        }

        public Criteria andSensorTypeGreaterThan(String value) {
            addCriterion("SENSOR_TYPE >", value, "sensorType");
            return (Criteria) this;
        }

        public Criteria andSensorTypeGreaterThanOrEqualTo(String value) {
            addCriterion("SENSOR_TYPE >=", value, "sensorType");
            return (Criteria) this;
        }

        public Criteria andSensorTypeLessThan(String value) {
            addCriterion("SENSOR_TYPE <", value, "sensorType");
            return (Criteria) this;
        }

        public Criteria andSensorTypeLessThanOrEqualTo(String value) {
            addCriterion("SENSOR_TYPE <=", value, "sensorType");
            return (Criteria) this;
        }

        public Criteria andSensorTypeLike(String value) {
            addCriterion("SENSOR_TYPE like", value, "sensorType");
            return (Criteria) this;
        }

        public Criteria andSensorTypeNotLike(String value) {
            addCriterion("SENSOR_TYPE not like", value, "sensorType");
            return (Criteria) this;
        }

        public Criteria andSensorTypeIn(List<String> values) {
            addCriterion("SENSOR_TYPE in", values, "sensorType");
            return (Criteria) this;
        }

        public Criteria andSensorTypeNotIn(List<String> values) {
            addCriterion("SENSOR_TYPE not in", values, "sensorType");
            return (Criteria) this;
        }

        public Criteria andSensorTypeBetween(String value1, String value2) {
            addCriterion("SENSOR_TYPE between", value1, value2, "sensorType");
            return (Criteria) this;
        }

        public Criteria andSensorTypeNotBetween(String value1, String value2) {
            addCriterion("SENSOR_TYPE not between", value1, value2, "sensorType");
            return (Criteria) this;
        }

        public Criteria andTimeJoinIsNull() {
            addCriterion("TIME_JOIN is null");
            return (Criteria) this;
        }

        public Criteria andTimeJoinIsNotNull() {
            addCriterion("TIME_JOIN is not null");
            return (Criteria) this;
        }

        public Criteria andTimeJoinEqualTo(String value) {
            addCriterion("TIME_JOIN =", value, "timeJoin");
            return (Criteria) this;
        }

        public Criteria andTimeJoinNotEqualTo(String value) {
            addCriterion("TIME_JOIN <>", value, "timeJoin");
            return (Criteria) this;
        }

        public Criteria andTimeJoinGreaterThan(String value) {
            addCriterion("TIME_JOIN >", value, "timeJoin");
            return (Criteria) this;
        }

        public Criteria andTimeJoinGreaterThanOrEqualTo(String value) {
            addCriterion("TIME_JOIN >=", value, "timeJoin");
            return (Criteria) this;
        }

        public Criteria andTimeJoinLessThan(String value) {
            addCriterion("TIME_JOIN <", value, "timeJoin");
            return (Criteria) this;
        }

        public Criteria andTimeJoinLessThanOrEqualTo(String value) {
            addCriterion("TIME_JOIN <=", value, "timeJoin");
            return (Criteria) this;
        }

        public Criteria andTimeJoinLike(String value) {
            addCriterion("TIME_JOIN like", value, "timeJoin");
            return (Criteria) this;
        }

        public Criteria andTimeJoinNotLike(String value) {
            addCriterion("TIME_JOIN not like", value, "timeJoin");
            return (Criteria) this;
        }

        public Criteria andTimeJoinIn(List<String> values) {
            addCriterion("TIME_JOIN in", values, "timeJoin");
            return (Criteria) this;
        }

        public Criteria andTimeJoinNotIn(List<String> values) {
            addCriterion("TIME_JOIN not in", values, "timeJoin");
            return (Criteria) this;
        }

        public Criteria andTimeJoinBetween(String value1, String value2) {
            addCriterion("TIME_JOIN between", value1, value2, "timeJoin");
            return (Criteria) this;
        }

        public Criteria andTimeJoinNotBetween(String value1, String value2) {
            addCriterion("TIME_JOIN not between", value1, value2, "timeJoin");
            return (Criteria) this;
        }

        public Criteria andTimeRecentlyIsNull() {
            addCriterion("TIME_RECENTLY is null");
            return (Criteria) this;
        }

        public Criteria andTimeRecentlyIsNotNull() {
            addCriterion("TIME_RECENTLY is not null");
            return (Criteria) this;
        }

        public Criteria andTimeRecentlyEqualTo(String value) {
            addCriterion("TIME_RECENTLY =", value, "timeRecently");
            return (Criteria) this;
        }

        public Criteria andTimeRecentlyNotEqualTo(String value) {
            addCriterion("TIME_RECENTLY <>", value, "timeRecently");
            return (Criteria) this;
        }

        public Criteria andTimeRecentlyGreaterThan(String value) {
            addCriterion("TIME_RECENTLY >", value, "timeRecently");
            return (Criteria) this;
        }

        public Criteria andTimeRecentlyGreaterThanOrEqualTo(String value) {
            addCriterion("TIME_RECENTLY >=", value, "timeRecently");
            return (Criteria) this;
        }

        public Criteria andTimeRecentlyLessThan(String value) {
            addCriterion("TIME_RECENTLY <", value, "timeRecently");
            return (Criteria) this;
        }

        public Criteria andTimeRecentlyLessThanOrEqualTo(String value) {
            addCriterion("TIME_RECENTLY <=", value, "timeRecently");
            return (Criteria) this;
        }

        public Criteria andTimeRecentlyLike(String value) {
            addCriterion("TIME_RECENTLY like", value, "timeRecently");
            return (Criteria) this;
        }

        public Criteria andTimeRecentlyNotLike(String value) {
            addCriterion("TIME_RECENTLY not like", value, "timeRecently");
            return (Criteria) this;
        }

        public Criteria andTimeRecentlyIn(List<String> values) {
            addCriterion("TIME_RECENTLY in", values, "timeRecently");
            return (Criteria) this;
        }

        public Criteria andTimeRecentlyNotIn(List<String> values) {
            addCriterion("TIME_RECENTLY not in", values, "timeRecently");
            return (Criteria) this;
        }

        public Criteria andTimeRecentlyBetween(String value1, String value2) {
            addCriterion("TIME_RECENTLY between", value1, value2, "timeRecently");
            return (Criteria) this;
        }

        public Criteria andTimeRecentlyNotBetween(String value1, String value2) {
            addCriterion("TIME_RECENTLY not between", value1, value2, "timeRecently");
            return (Criteria) this;
        }

        public Criteria andSensorNameIsNull() {
            addCriterion("SENSOR_NAME is null");
            return (Criteria) this;
        }

        public Criteria andSensorNameIsNotNull() {
            addCriterion("SENSOR_NAME is not null");
            return (Criteria) this;
        }

        public Criteria andSensorNameEqualTo(String value) {
            addCriterion("SENSOR_NAME =", value, "sensorName");
            return (Criteria) this;
        }

        public Criteria andSensorNameNotEqualTo(String value) {
            addCriterion("SENSOR_NAME <>", value, "sensorName");
            return (Criteria) this;
        }

        public Criteria andSensorNameGreaterThan(String value) {
            addCriterion("SENSOR_NAME >", value, "sensorName");
            return (Criteria) this;
        }

        public Criteria andSensorNameGreaterThanOrEqualTo(String value) {
            addCriterion("SENSOR_NAME >=", value, "sensorName");
            return (Criteria) this;
        }

        public Criteria andSensorNameLessThan(String value) {
            addCriterion("SENSOR_NAME <", value, "sensorName");
            return (Criteria) this;
        }

        public Criteria andSensorNameLessThanOrEqualTo(String value) {
            addCriterion("SENSOR_NAME <=", value, "sensorName");
            return (Criteria) this;
        }

        public Criteria andSensorNameLike(String value) {
            addCriterion("SENSOR_NAME like", value, "sensorName");
            return (Criteria) this;
        }

        public Criteria andSensorNameNotLike(String value) {
            addCriterion("SENSOR_NAME not like", value, "sensorName");
            return (Criteria) this;
        }

        public Criteria andSensorNameIn(List<String> values) {
            addCriterion("SENSOR_NAME in", values, "sensorName");
            return (Criteria) this;
        }

        public Criteria andSensorNameNotIn(List<String> values) {
            addCriterion("SENSOR_NAME not in", values, "sensorName");
            return (Criteria) this;
        }

        public Criteria andSensorNameBetween(String value1, String value2) {
            addCriterion("SENSOR_NAME between", value1, value2, "sensorName");
            return (Criteria) this;
        }

        public Criteria andSensorNameNotBetween(String value1, String value2) {
            addCriterion("SENSOR_NAME not between", value1, value2, "sensorName");
            return (Criteria) this;
        }

        public Criteria andStatusIsNull() {
            addCriterion("STATUS is null");
            return (Criteria) this;
        }

        public Criteria andStatusIsNotNull() {
            addCriterion("STATUS is not null");
            return (Criteria) this;
        }

        public Criteria andStatusEqualTo(String value) {
            addCriterion("STATUS =", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusNotEqualTo(String value) {
            addCriterion("STATUS <>", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusGreaterThan(String value) {
            addCriterion("STATUS >", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusGreaterThanOrEqualTo(String value) {
            addCriterion("STATUS >=", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusLessThan(String value) {
            addCriterion("STATUS <", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusLessThanOrEqualTo(String value) {
            addCriterion("STATUS <=", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusLike(String value) {
            addCriterion("STATUS like", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusNotLike(String value) {
            addCriterion("STATUS not like", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusIn(List<String> values) {
            addCriterion("STATUS in", values, "status");
            return (Criteria) this;
        }

        public Criteria andStatusNotIn(List<String> values) {
            addCriterion("STATUS not in", values, "status");
            return (Criteria) this;
        }

        public Criteria andStatusBetween(String value1, String value2) {
            addCriterion("STATUS between", value1, value2, "status");
            return (Criteria) this;
        }

        public Criteria andStatusNotBetween(String value1, String value2) {
            addCriterion("STATUS not between", value1, value2, "status");
            return (Criteria) this;
        }

        public Criteria andMacIsNull() {
            addCriterion("MAC is null");
            return (Criteria) this;
        }

        public Criteria andMacIsNotNull() {
            addCriterion("MAC is not null");
            return (Criteria) this;
        }

        public Criteria andMacEqualTo(String value) {
            addCriterion("MAC =", value, "mac");
            return (Criteria) this;
        }

        public Criteria andMacNotEqualTo(String value) {
            addCriterion("MAC <>", value, "mac");
            return (Criteria) this;
        }

        public Criteria andMacGreaterThan(String value) {
            addCriterion("MAC >", value, "mac");
            return (Criteria) this;
        }

        public Criteria andMacGreaterThanOrEqualTo(String value) {
            addCriterion("MAC >=", value, "mac");
            return (Criteria) this;
        }

        public Criteria andMacLessThan(String value) {
            addCriterion("MAC <", value, "mac");
            return (Criteria) this;
        }

        public Criteria andMacLessThanOrEqualTo(String value) {
            addCriterion("MAC <=", value, "mac");
            return (Criteria) this;
        }

        public Criteria andMacLike(String value) {
            addCriterion("MAC like", value, "mac");
            return (Criteria) this;
        }

        public Criteria andMacNotLike(String value) {
            addCriterion("MAC not like", value, "mac");
            return (Criteria) this;
        }

        public Criteria andMacIn(List<String> values) {
            addCriterion("MAC in", values, "mac");
            return (Criteria) this;
        }

        public Criteria andMacNotIn(List<String> values) {
            addCriterion("MAC not in", values, "mac");
            return (Criteria) this;
        }

        public Criteria andMacBetween(String value1, String value2) {
            addCriterion("MAC between", value1, value2, "mac");
            return (Criteria) this;
        }

        public Criteria andMacNotBetween(String value1, String value2) {
            addCriterion("MAC not between", value1, value2, "mac");
            return (Criteria) this;
        }

        public Criteria andGroupIdIsNull() {
            addCriterion("GROUP_ID is null");
            return (Criteria) this;
        }

        public Criteria andGroupIdIsNotNull() {
            addCriterion("GROUP_ID is not null");
            return (Criteria) this;
        }

        public Criteria andGroupIdEqualTo(String value) {
            addCriterion("GROUP_ID =", value, "groupId");
            return (Criteria) this;
        }

        public Criteria andGroupIdNotEqualTo(String value) {
            addCriterion("GROUP_ID <>", value, "groupId");
            return (Criteria) this;
        }

        public Criteria andGroupIdGreaterThan(String value) {
            addCriterion("GROUP_ID >", value, "groupId");
            return (Criteria) this;
        }

        public Criteria andGroupIdGreaterThanOrEqualTo(String value) {
            addCriterion("GROUP_ID >=", value, "groupId");
            return (Criteria) this;
        }

        public Criteria andGroupIdLessThan(String value) {
            addCriterion("GROUP_ID <", value, "groupId");
            return (Criteria) this;
        }

        public Criteria andGroupIdLessThanOrEqualTo(String value) {
            addCriterion("GROUP_ID <=", value, "groupId");
            return (Criteria) this;
        }

        public Criteria andGroupIdLike(String value) {
            addCriterion("GROUP_ID like", value, "groupId");
            return (Criteria) this;
        }

        public Criteria andGroupIdNotLike(String value) {
            addCriterion("GROUP_ID not like", value, "groupId");
            return (Criteria) this;
        }

        public Criteria andGroupIdIn(List<String> values) {
            addCriterion("GROUP_ID in", values, "groupId");
            return (Criteria) this;
        }

        public Criteria andGroupIdNotIn(List<String> values) {
            addCriterion("GROUP_ID not in", values, "groupId");
            return (Criteria) this;
        }

        public Criteria andGroupIdBetween(String value1, String value2) {
            addCriterion("GROUP_ID between", value1, value2, "groupId");
            return (Criteria) this;
        }

        public Criteria andGroupIdNotBetween(String value1, String value2) {
            addCriterion("GROUP_ID not between", value1, value2, "groupId");
            return (Criteria) this;
        }

        public Criteria andAvatarIsNull() {
            addCriterion("AVATAR is null");
            return (Criteria) this;
        }

        public Criteria andAvatarIsNotNull() {
            addCriterion("AVATAR is not null");
            return (Criteria) this;
        }

        public Criteria andAvatarEqualTo(String value) {
            addCriterion("AVATAR =", value, "avatar");
            return (Criteria) this;
        }

        public Criteria andAvatarNotEqualTo(String value) {
            addCriterion("AVATAR <>", value, "avatar");
            return (Criteria) this;
        }

        public Criteria andAvatarGreaterThan(String value) {
            addCriterion("AVATAR >", value, "avatar");
            return (Criteria) this;
        }

        public Criteria andAvatarGreaterThanOrEqualTo(String value) {
            addCriterion("AVATAR >=", value, "avatar");
            return (Criteria) this;
        }

        public Criteria andAvatarLessThan(String value) {
            addCriterion("AVATAR <", value, "avatar");
            return (Criteria) this;
        }

        public Criteria andAvatarLessThanOrEqualTo(String value) {
            addCriterion("AVATAR <=", value, "avatar");
            return (Criteria) this;
        }

        public Criteria andAvatarLike(String value) {
            addCriterion("AVATAR like", value, "avatar");
            return (Criteria) this;
        }

        public Criteria andAvatarNotLike(String value) {
            addCriterion("AVATAR not like", value, "avatar");
            return (Criteria) this;
        }

        public Criteria andAvatarIn(List<String> values) {
            addCriterion("AVATAR in", values, "avatar");
            return (Criteria) this;
        }

        public Criteria andAvatarNotIn(List<String> values) {
            addCriterion("AVATAR not in", values, "avatar");
            return (Criteria) this;
        }

        public Criteria andAvatarBetween(String value1, String value2) {
            addCriterion("AVATAR between", value1, value2, "avatar");
            return (Criteria) this;
        }

        public Criteria andAvatarNotBetween(String value1, String value2) {
            addCriterion("AVATAR not between", value1, value2, "avatar");
            return (Criteria) this;
        }

        public Criteria andMongoTableIsNull() {
            addCriterion("MONGO_TABLE is null");
            return (Criteria) this;
        }

        public Criteria andMongoTableIsNotNull() {
            addCriterion("MONGO_TABLE is not null");
            return (Criteria) this;
        }

        public Criteria andMongoTableEqualTo(String value) {
            addCriterion("MONGO_TABLE =", value, "mongoTable");
            return (Criteria) this;
        }

        public Criteria andMongoTableNotEqualTo(String value) {
            addCriterion("MONGO_TABLE <>", value, "mongoTable");
            return (Criteria) this;
        }

        public Criteria andMongoTableGreaterThan(String value) {
            addCriterion("MONGO_TABLE >", value, "mongoTable");
            return (Criteria) this;
        }

        public Criteria andMongoTableGreaterThanOrEqualTo(String value) {
            addCriterion("MONGO_TABLE >=", value, "mongoTable");
            return (Criteria) this;
        }

        public Criteria andMongoTableLessThan(String value) {
            addCriterion("MONGO_TABLE <", value, "mongoTable");
            return (Criteria) this;
        }

        public Criteria andMongoTableLessThanOrEqualTo(String value) {
            addCriterion("MONGO_TABLE <=", value, "mongoTable");
            return (Criteria) this;
        }

        public Criteria andMongoTableLike(String value) {
            addCriterion("MONGO_TABLE like", value, "mongoTable");
            return (Criteria) this;
        }

        public Criteria andMongoTableNotLike(String value) {
            addCriterion("MONGO_TABLE not like", value, "mongoTable");
            return (Criteria) this;
        }

        public Criteria andMongoTableIn(List<String> values) {
            addCriterion("MONGO_TABLE in", values, "mongoTable");
            return (Criteria) this;
        }

        public Criteria andMongoTableNotIn(List<String> values) {
            addCriterion("MONGO_TABLE not in", values, "mongoTable");
            return (Criteria) this;
        }

        public Criteria andMongoTableBetween(String value1, String value2) {
            addCriterion("MONGO_TABLE between", value1, value2, "mongoTable");
            return (Criteria) this;
        }

        public Criteria andMongoTableNotBetween(String value1, String value2) {
            addCriterion("MONGO_TABLE not between", value1, value2, "mongoTable");
            return (Criteria) this;
        }

        public Criteria andProjectUuidIsNull() {
            addCriterion("PROJECT_UUID is null");
            return (Criteria) this;
        }

        public Criteria andProjectUuidIsNotNull() {
            addCriterion("PROJECT_UUID is not null");
            return (Criteria) this;
        }

        public Criteria andProjectUuidEqualTo(String value) {
            addCriterion("PROJECT_UUID =", value, "projectUuid");
            return (Criteria) this;
        }

        public Criteria andProjectUuidNotEqualTo(String value) {
            addCriterion("PROJECT_UUID <>", value, "projectUuid");
            return (Criteria) this;
        }

        public Criteria andProjectUuidGreaterThan(String value) {
            addCriterion("PROJECT_UUID >", value, "projectUuid");
            return (Criteria) this;
        }

        public Criteria andProjectUuidGreaterThanOrEqualTo(String value) {
            addCriterion("PROJECT_UUID >=", value, "projectUuid");
            return (Criteria) this;
        }

        public Criteria andProjectUuidLessThan(String value) {
            addCriterion("PROJECT_UUID <", value, "projectUuid");
            return (Criteria) this;
        }

        public Criteria andProjectUuidLessThanOrEqualTo(String value) {
            addCriterion("PROJECT_UUID <=", value, "projectUuid");
            return (Criteria) this;
        }

        public Criteria andProjectUuidLike(String value) {
            addCriterion("PROJECT_UUID like", value, "projectUuid");
            return (Criteria) this;
        }

        public Criteria andProjectUuidNotLike(String value) {
            addCriterion("PROJECT_UUID not like", value, "projectUuid");
            return (Criteria) this;
        }

        public Criteria andProjectUuidIn(List<String> values) {
            addCriterion("PROJECT_UUID in", values, "projectUuid");
            return (Criteria) this;
        }

        public Criteria andProjectUuidNotIn(List<String> values) {
            addCriterion("PROJECT_UUID not in", values, "projectUuid");
            return (Criteria) this;
        }

        public Criteria andProjectUuidBetween(String value1, String value2) {
            addCriterion("PROJECT_UUID between", value1, value2, "projectUuid");
            return (Criteria) this;
        }

        public Criteria andProjectUuidNotBetween(String value1, String value2) {
            addCriterion("PROJECT_UUID not between", value1, value2, "projectUuid");
            return (Criteria) this;
        }

        public Criteria andIsstoppedIsNull() {
            addCriterion("IsStopped is null");
            return (Criteria) this;
        }

        public Criteria andIsstoppedIsNotNull() {
            addCriterion("IsStopped is not null");
            return (Criteria) this;
        }

        public Criteria andIsstoppedEqualTo(String value) {
            addCriterion("IsStopped =", value, "isstopped");
            return (Criteria) this;
        }

        public Criteria andIsstoppedNotEqualTo(String value) {
            addCriterion("IsStopped <>", value, "isstopped");
            return (Criteria) this;
        }

        public Criteria andIsstoppedGreaterThan(String value) {
            addCriterion("IsStopped >", value, "isstopped");
            return (Criteria) this;
        }

        public Criteria andIsstoppedGreaterThanOrEqualTo(String value) {
            addCriterion("IsStopped >=", value, "isstopped");
            return (Criteria) this;
        }

        public Criteria andIsstoppedLessThan(String value) {
            addCriterion("IsStopped <", value, "isstopped");
            return (Criteria) this;
        }

        public Criteria andIsstoppedLessThanOrEqualTo(String value) {
            addCriterion("IsStopped <=", value, "isstopped");
            return (Criteria) this;
        }

        public Criteria andIsstoppedLike(String value) {
            addCriterion("IsStopped like", value, "isstopped");
            return (Criteria) this;
        }

        public Criteria andIsstoppedNotLike(String value) {
            addCriterion("IsStopped not like", value, "isstopped");
            return (Criteria) this;
        }

        public Criteria andIsstoppedIn(List<String> values) {
            addCriterion("IsStopped in", values, "isstopped");
            return (Criteria) this;
        }

        public Criteria andIsstoppedNotIn(List<String> values) {
            addCriterion("IsStopped not in", values, "isstopped");
            return (Criteria) this;
        }

        public Criteria andIsstoppedBetween(String value1, String value2) {
            addCriterion("IsStopped between", value1, value2, "isstopped");
            return (Criteria) this;
        }

        public Criteria andIsstoppedNotBetween(String value1, String value2) {
            addCriterion("IsStopped not between", value1, value2, "isstopped");
            return (Criteria) this;
        }

        public Criteria andSecondchecktimeIsNull() {
            addCriterion("SecondCheckTime is null");
            return (Criteria) this;
        }

        public Criteria andSecondchecktimeIsNotNull() {
            addCriterion("SecondCheckTime is not null");
            return (Criteria) this;
        }

        public Criteria andSecondchecktimeEqualTo(Integer value) {
            addCriterion("SecondCheckTime =", value, "secondchecktime");
            return (Criteria) this;
        }

        public Criteria andSecondchecktimeNotEqualTo(Integer value) {
            addCriterion("SecondCheckTime <>", value, "secondchecktime");
            return (Criteria) this;
        }

        public Criteria andSecondchecktimeGreaterThan(Integer value) {
            addCriterion("SecondCheckTime >", value, "secondchecktime");
            return (Criteria) this;
        }

        public Criteria andSecondchecktimeGreaterThanOrEqualTo(Integer value) {
            addCriterion("SecondCheckTime >=", value, "secondchecktime");
            return (Criteria) this;
        }

        public Criteria andSecondchecktimeLessThan(Integer value) {
            addCriterion("SecondCheckTime <", value, "secondchecktime");
            return (Criteria) this;
        }

        public Criteria andSecondchecktimeLessThanOrEqualTo(Integer value) {
            addCriterion("SecondCheckTime <=", value, "secondchecktime");
            return (Criteria) this;
        }

        public Criteria andSecondchecktimeIn(List<Integer> values) {
            addCriterion("SecondCheckTime in", values, "secondchecktime");
            return (Criteria) this;
        }

        public Criteria andSecondchecktimeNotIn(List<Integer> values) {
            addCriterion("SecondCheckTime not in", values, "secondchecktime");
            return (Criteria) this;
        }

        public Criteria andSecondchecktimeBetween(Integer value1, Integer value2) {
            addCriterion("SecondCheckTime between", value1, value2, "secondchecktime");
            return (Criteria) this;
        }

        public Criteria andSecondchecktimeNotBetween(Integer value1, Integer value2) {
            addCriterion("SecondCheckTime not between", value1, value2, "secondchecktime");
            return (Criteria) this;
        }

        public Criteria andStartvalueIsNull() {
            addCriterion("StartValue is null");
            return (Criteria) this;
        }

        public Criteria andStartvalueIsNotNull() {
            addCriterion("StartValue is not null");
            return (Criteria) this;
        }

        public Criteria andStartvalueEqualTo(String value) {
            addCriterion("StartValue =", value, "startvalue");
            return (Criteria) this;
        }

        public Criteria andStartvalueNotEqualTo(String value) {
            addCriterion("StartValue <>", value, "startvalue");
            return (Criteria) this;
        }

        public Criteria andStartvalueGreaterThan(String value) {
            addCriterion("StartValue >", value, "startvalue");
            return (Criteria) this;
        }

        public Criteria andStartvalueGreaterThanOrEqualTo(String value) {
            addCriterion("StartValue >=", value, "startvalue");
            return (Criteria) this;
        }

        public Criteria andStartvalueLessThan(String value) {
            addCriterion("StartValue <", value, "startvalue");
            return (Criteria) this;
        }

        public Criteria andStartvalueLessThanOrEqualTo(String value) {
            addCriterion("StartValue <=", value, "startvalue");
            return (Criteria) this;
        }

        public Criteria andStartvalueLike(String value) {
            addCriterion("StartValue like", value, "startvalue");
            return (Criteria) this;
        }

        public Criteria andStartvalueNotLike(String value) {
            addCriterion("StartValue not like", value, "startvalue");
            return (Criteria) this;
        }

        public Criteria andStartvalueIn(List<String> values) {
            addCriterion("StartValue in", values, "startvalue");
            return (Criteria) this;
        }

        public Criteria andStartvalueNotIn(List<String> values) {
            addCriterion("StartValue not in", values, "startvalue");
            return (Criteria) this;
        }

        public Criteria andStartvalueBetween(String value1, String value2) {
            addCriterion("StartValue between", value1, value2, "startvalue");
            return (Criteria) this;
        }

        public Criteria andStartvalueNotBetween(String value1, String value2) {
            addCriterion("StartValue not between", value1, value2, "startvalue");
            return (Criteria) this;
        }

        public Criteria andRuleidIsNull() {
            addCriterion("RuleId is null");
            return (Criteria) this;
        }

        public Criteria andRuleidIsNotNull() {
            addCriterion("RuleId is not null");
            return (Criteria) this;
        }

        public Criteria andRuleidEqualTo(Integer value) {
            addCriterion("RuleId =", value, "ruleid");
            return (Criteria) this;
        }

        public Criteria andRuleidNotEqualTo(Integer value) {
            addCriterion("RuleId <>", value, "ruleid");
            return (Criteria) this;
        }

        public Criteria andRuleidGreaterThan(Integer value) {
            addCriterion("RuleId >", value, "ruleid");
            return (Criteria) this;
        }

        public Criteria andRuleidGreaterThanOrEqualTo(Integer value) {
            addCriterion("RuleId >=", value, "ruleid");
            return (Criteria) this;
        }

        public Criteria andRuleidLessThan(Integer value) {
            addCriterion("RuleId <", value, "ruleid");
            return (Criteria) this;
        }

        public Criteria andRuleidLessThanOrEqualTo(Integer value) {
            addCriterion("RuleId <=", value, "ruleid");
            return (Criteria) this;
        }

        public Criteria andRuleidIn(List<Integer> values) {
            addCriterion("RuleId in", values, "ruleid");
            return (Criteria) this;
        }

        public Criteria andRuleidNotIn(List<Integer> values) {
            addCriterion("RuleId not in", values, "ruleid");
            return (Criteria) this;
        }

        public Criteria andRuleidBetween(Integer value1, Integer value2) {
            addCriterion("RuleId between", value1, value2, "ruleid");
            return (Criteria) this;
        }

        public Criteria andRuleidNotBetween(Integer value1, Integer value2) {
            addCriterion("RuleId not between", value1, value2, "ruleid");
            return (Criteria) this;
        }

        public Criteria andSerialnumIsNull() {
            addCriterion("SerialNum is null");
            return (Criteria) this;
        }

        public Criteria andSerialnumIsNotNull() {
            addCriterion("SerialNum is not null");
            return (Criteria) this;
        }

        public Criteria andSerialnumEqualTo(String value) {
            addCriterion("SerialNum =", value, "serialnum");
            return (Criteria) this;
        }

        public Criteria andSerialnumNotEqualTo(String value) {
            addCriterion("SerialNum <>", value, "serialnum");
            return (Criteria) this;
        }

        public Criteria andSerialnumGreaterThan(String value) {
            addCriterion("SerialNum >", value, "serialnum");
            return (Criteria) this;
        }

        public Criteria andSerialnumGreaterThanOrEqualTo(String value) {
            addCriterion("SerialNum >=", value, "serialnum");
            return (Criteria) this;
        }

        public Criteria andSerialnumLessThan(String value) {
            addCriterion("SerialNum <", value, "serialnum");
            return (Criteria) this;
        }

        public Criteria andSerialnumLessThanOrEqualTo(String value) {
            addCriterion("SerialNum <=", value, "serialnum");
            return (Criteria) this;
        }

        public Criteria andSerialnumLike(String value) {
            addCriterion("SerialNum like", value, "serialnum");
            return (Criteria) this;
        }

        public Criteria andSerialnumNotLike(String value) {
            addCriterion("SerialNum not like", value, "serialnum");
            return (Criteria) this;
        }

        public Criteria andSerialnumIn(List<String> values) {
            addCriterion("SerialNum in", values, "serialnum");
            return (Criteria) this;
        }

        public Criteria andSerialnumNotIn(List<String> values) {
            addCriterion("SerialNum not in", values, "serialnum");
            return (Criteria) this;
        }

        public Criteria andSerialnumBetween(String value1, String value2) {
            addCriterion("SerialNum between", value1, value2, "serialnum");
            return (Criteria) this;
        }

        public Criteria andSerialnumNotBetween(String value1, String value2) {
            addCriterion("SerialNum not between", value1, value2, "serialnum");
            return (Criteria) this;
        }

        public Criteria andFuncsIsNull() {
            addCriterion("Funcs is null");
            return (Criteria) this;
        }

        public Criteria andFuncsIsNotNull() {
            addCriterion("Funcs is not null");
            return (Criteria) this;
        }

        public Criteria andFuncsEqualTo(String value) {
            addCriterion("Funcs =", value, "funcs");
            return (Criteria) this;
        }

        public Criteria andFuncsNotEqualTo(String value) {
            addCriterion("Funcs <>", value, "funcs");
            return (Criteria) this;
        }

        public Criteria andFuncsGreaterThan(String value) {
            addCriterion("Funcs >", value, "funcs");
            return (Criteria) this;
        }

        public Criteria andFuncsGreaterThanOrEqualTo(String value) {
            addCriterion("Funcs >=", value, "funcs");
            return (Criteria) this;
        }

        public Criteria andFuncsLessThan(String value) {
            addCriterion("Funcs <", value, "funcs");
            return (Criteria) this;
        }

        public Criteria andFuncsLessThanOrEqualTo(String value) {
            addCriterion("Funcs <=", value, "funcs");
            return (Criteria) this;
        }

        public Criteria andFuncsLike(String value) {
            addCriterion("Funcs like", value, "funcs");
            return (Criteria) this;
        }

        public Criteria andFuncsNotLike(String value) {
            addCriterion("Funcs not like", value, "funcs");
            return (Criteria) this;
        }

        public Criteria andFuncsIn(List<String> values) {
            addCriterion("Funcs in", values, "funcs");
            return (Criteria) this;
        }

        public Criteria andFuncsNotIn(List<String> values) {
            addCriterion("Funcs not in", values, "funcs");
            return (Criteria) this;
        }

        public Criteria andFuncsBetween(String value1, String value2) {
            addCriterion("Funcs between", value1, value2, "funcs");
            return (Criteria) this;
        }

        public Criteria andFuncsNotBetween(String value1, String value2) {
            addCriterion("Funcs not between", value1, value2, "funcs");
            return (Criteria) this;
        }

        public Criteria andRealrateIsNull() {
            addCriterion("RealRate is null");
            return (Criteria) this;
        }

        public Criteria andRealrateIsNotNull() {
            addCriterion("RealRate is not null");
            return (Criteria) this;
        }

        public Criteria andRealrateEqualTo(BigDecimal value) {
            addCriterion("RealRate =", value, "realrate");
            return (Criteria) this;
        }

        public Criteria andRealrateNotEqualTo(BigDecimal value) {
            addCriterion("RealRate <>", value, "realrate");
            return (Criteria) this;
        }

        public Criteria andRealrateGreaterThan(BigDecimal value) {
            addCriterion("RealRate >", value, "realrate");
            return (Criteria) this;
        }

        public Criteria andRealrateGreaterThanOrEqualTo(BigDecimal value) {
            addCriterion("RealRate >=", value, "realrate");
            return (Criteria) this;
        }

        public Criteria andRealrateLessThan(BigDecimal value) {
            addCriterion("RealRate <", value, "realrate");
            return (Criteria) this;
        }

        public Criteria andRealrateLessThanOrEqualTo(BigDecimal value) {
            addCriterion("RealRate <=", value, "realrate");
            return (Criteria) this;
        }

        public Criteria andRealrateIn(List<BigDecimal> values) {
            addCriterion("RealRate in", values, "realrate");
            return (Criteria) this;
        }

        public Criteria andRealrateNotIn(List<BigDecimal> values) {
            addCriterion("RealRate not in", values, "realrate");
            return (Criteria) this;
        }

        public Criteria andRealrateBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("RealRate between", value1, value2, "realrate");
            return (Criteria) this;
        }

        public Criteria andRealrateNotBetween(BigDecimal value1, BigDecimal value2) {
            addCriterion("RealRate not between", value1, value2, "realrate");
            return (Criteria) this;
        }

        public Criteria andVisibleIsNull() {
            addCriterion("Visible is null");
            return (Criteria) this;
        }

        public Criteria andVisibleIsNotNull() {
            addCriterion("Visible is not null");
            return (Criteria) this;
        }

        public Criteria andVisibleEqualTo(Integer value) {
            addCriterion("Visible =", value, "visible");
            return (Criteria) this;
        }

        public Criteria andVisibleNotEqualTo(Integer value) {
            addCriterion("Visible <>", value, "visible");
            return (Criteria) this;
        }

        public Criteria andVisibleGreaterThan(Integer value) {
            addCriterion("Visible >", value, "visible");
            return (Criteria) this;
        }

        public Criteria andVisibleGreaterThanOrEqualTo(Integer value) {
            addCriterion("Visible >=", value, "visible");
            return (Criteria) this;
        }

        public Criteria andVisibleLessThan(Integer value) {
            addCriterion("Visible <", value, "visible");
            return (Criteria) this;
        }

        public Criteria andVisibleLessThanOrEqualTo(Integer value) {
            addCriterion("Visible <=", value, "visible");
            return (Criteria) this;
        }

        public Criteria andVisibleIn(List<Integer> values) {
            addCriterion("Visible in", values, "visible");
            return (Criteria) this;
        }

        public Criteria andVisibleNotIn(List<Integer> values) {
            addCriterion("Visible not in", values, "visible");
            return (Criteria) this;
        }

        public Criteria andVisibleBetween(Integer value1, Integer value2) {
            addCriterion("Visible between", value1, value2, "visible");
            return (Criteria) this;
        }

        public Criteria andVisibleNotBetween(Integer value1, Integer value2) {
            addCriterion("Visible not between", value1, value2, "visible");
            return (Criteria) this;
        }
    }

    public static class Criteria extends GeneratedCriteria {

        protected Criteria() {
            super();
        }
    }

    public static class Criterion {
        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }
    }
}