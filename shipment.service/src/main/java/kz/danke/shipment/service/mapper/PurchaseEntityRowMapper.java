package kz.danke.shipment.service.mapper;

import kz.danke.shipment.service.model.Purchase;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PurchaseEntityRowMapper implements RowMapper<Purchase> {

    @Override
    public Purchase mapRow(ResultSet resultSet, int i) throws SQLException {
        return Purchase.builder()
                .id(resultSet.getLong("id"))
                .firstName(resultSet.getString("firstname"))
                .lastName(resultSet.getString("lastname"))
                .city(resultSet.getString("city"))
                .phoneNumber(resultSet.getString("phone_number"))
                .email(resultSet.getString("email"))
                .address(resultSet.getString("address"))
                .businessKey(resultSet.getString("business_key"))
                .version(resultSet.getShort("version"))
                .sent(resultSet.getBoolean("sent"))
                .build();
    }
}
