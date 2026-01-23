package com.chatbot.common.handler;

import com.chatbot.model.entity.Message;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Administrator
 */
public class MessageSenderTypeHandler extends BaseTypeHandler<Message.SenderType> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Message.SenderType parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter.getCode());
    }

    @Override
    public Message.SenderType getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return fromCode(rs.getString(columnName));
    }

    @Override
    public Message.SenderType getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return fromCode(rs.getString(columnIndex));
    }

    @Override
    public Message.SenderType getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return fromCode(cs.getString(columnIndex));
    }

    private Message.SenderType fromCode(String code) {
        if (code == null) {
            return null;
        }
        for (Message.SenderType value : Message.SenderType.values()) {
            if (code.equalsIgnoreCase(value.getCode())) {
                return value;
            }
        }
        return null;
    }
}

