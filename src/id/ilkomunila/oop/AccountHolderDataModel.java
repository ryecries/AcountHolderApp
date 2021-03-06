/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.ilkomunila.oop;

import id.ilkomunila.oop.db.DBHelper;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author didik
 */
public class AccountHolderDataModel {
    public final Connection conn;

    public AccountHolderDataModel(String driver) throws SQLException {
        this.conn = DBHelper.getConnection(driver);
    }
    public void addAccountHolder(IndividualHolder holder) throws SQLException{
        String insertHolder = "INSERT INTO account_holder (holder_id, name, address)"
                + " VALUES (?,?,?)";
        String insertIndividual = "INSERT INTO individual_holder (holder_id, gender, birthdate)"
                + " VALUES (?,?,?)";
        String insertAccount = "INSERT INTO account (acc_number, balance, holder_id)"
                + " VALUES (?,?,?)";
        PreparedStatement stmtHolder = conn.prepareStatement(insertHolder);
        stmtHolder.setInt(1, holder.getHolderID());
        stmtHolder.setString(2, holder.getName());
        stmtHolder.setString(3, holder.getAddress());
        stmtHolder.execute();
        
        PreparedStatement stmtIndividual = conn.prepareStatement(insertIndividual);
        stmtIndividual.setInt(1, holder.getHolderID());
        stmtIndividual.setString(2, holder.getGender());
        stmtIndividual.setString(3, holder.getBirthdate());
        stmtIndividual.execute();
        
        PreparedStatement stmtAccount = conn.prepareStatement(insertAccount);
        stmtAccount.setInt(1, holder.getAccounts().get(0).getAccNumber());
        stmtAccount.setDouble(2, holder.getAccounts().get(0).getBalance());
        stmtAccount.setInt(3, holder.getHolderID());
        stmtAccount.execute();
        
    }
    public void addAccountHolder(CorporateHolder holder) throws SQLException{
        String insertHolder = "INSERT INTO account_holder (holder_id, name, address)"
                + " VALUES (?,?,?)";
        String insertCorporate = "INSERT INTO corporate_holder (holder_id, contact)"
                + " VALUES (?,?)";
        String insertAccount = "INSERT INTO account (acc_number, balance, holder_id)"
                + " VALUES (?,?,?)";
        PreparedStatement stmtHolder = conn.prepareStatement(insertHolder);
        stmtHolder.setInt(1, holder.getHolderID());
        stmtHolder.setString(2, holder.getName());
        stmtHolder.setString(3, holder.getAddress());
        stmtHolder.execute();
        
        PreparedStatement stmtIndividual = conn.prepareStatement(insertCorporate);
        stmtIndividual.setInt(1, holder.getHolderID());
        stmtIndividual.setString(2, holder.getContact());
        stmtIndividual.execute();
        
        PreparedStatement stmtAccount = conn.prepareStatement(insertAccount);
        stmtAccount.setInt(1, holder.getAccounts().get(0).getAccNumber());
        stmtAccount.setDouble(2, holder.getAccounts().get(0).getBalance());
        stmtAccount.setInt(3, holder.getHolderID());
        stmtAccount.execute();
    }
    public ObservableList<IndividualHolder> getIndividualHolders(){
        ObservableList<IndividualHolder> data = FXCollections.observableArrayList();
        String sql="SELECT `holder_id`, `name`,`address`, `gender`, `birthdate` "
                + "FROM `account_holder` NATURAL JOIN `individual_holder` "
                + "ORDER BY name";
        try {
            ResultSet rs = conn.createStatement().executeQuery(sql);
            while (rs.next()){
                String sqlAccount = "SELECT acc_number, balance "
                    + "FROM account WHERE holder_id="+rs.getInt(1);
                ResultSet rsAccount = conn.createStatement().executeQuery(sqlAccount);
                ArrayList<Account> dataAccount = new ArrayList<>();
                while (rsAccount.next()){
                    dataAccount.add(new Account(rsAccount.getInt(1),rsAccount.getDouble(2)));
                }
                data.add(new IndividualHolder(rs.getInt(1),rs.getString(2),rs.getString(3),rs.getString(4),rs.getString(5), dataAccount));
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(AccountHolderDataModel.class.getName()).log(Level.SEVERE, null, ex);
        }     
        return data;
    }
    public ObservableList<CorporateHolder> getCorporateHolders(){
        ObservableList<CorporateHolder> data = FXCollections.observableArrayList();
        String sql="SELECT `holder_id`, `name`,`address`, `contact` "
                + "FROM `account_holder` NATURAL JOIN `corporate_holder` "
                + "ORDER BY name";
        try {
            ResultSet rs = conn.createStatement().executeQuery(sql);
            while (rs.next()){
                String sqlAccount = "SELECT acc_number, balance "
                    + "FROM account WHERE holder_id="+rs.getInt(1);
                ResultSet rsAccount = conn.createStatement().executeQuery(sqlAccount);
                ArrayList<Account> dataAccount = new ArrayList<>();
                while (rsAccount.next()){
                    dataAccount.add(new Account(rsAccount.getInt(1),rsAccount.getDouble(2)));
                }
                data.add(new CorporateHolder(rs.getInt(1),rs.getString(2),rs.getString(3),rs.getString(4), dataAccount));
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(AccountHolderDataModel.class.getName()).log(Level.SEVERE, null, ex);
        }     
        return data;
    }
    public ObservableList<Account> getAccounts(int holderID){
        ObservableList<Account> data = FXCollections.observableArrayList();
        String sql="SELECT `acc_number`, `balance` "
                + "FROM `account` "
                + "WHERE holder_id="+holderID;
        try {
            ResultSet rs = conn.createStatement().executeQuery(sql);
            while (rs.next()){
                data.add(new Account(rs.getInt(1),rs.getDouble(2)));
            }
        } catch (SQLException ex) {
            Logger.getLogger(AccountHolderDataModel.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return data;
    }
    public int nextAccountHolderID() throws SQLException{
        String sql="SELECT MAX(holder_id) from account_holder";
        ResultSet rs = conn.createStatement().executeQuery(sql);
        while (rs.next()){
                return rs.getInt(1)==0?1000001:rs.getInt(1)+1;
            }
        return 1000001;
    }
    public int nextAccountNumber(int holderID) throws SQLException{
        String sql="SELECT MAX(acc_number) FROM account WHERE holder_id="+holderID;
        ResultSet rs = conn.createStatement().executeQuery(sql);
        while (rs.next()){
                return rs.getInt(1)+1;
            }
        return 0;
    }
    
    public void addAccount(int holderID, Account acc) throws SQLException{
        String insertHolder = "INSERT INTO account (holder_id, acc_number, balance)"
                + " VALUES (?,?,?)";
  
        PreparedStatement stmtHolder = conn.prepareStatement(insertHolder);
        stmtHolder.setInt(1, holderID);
        stmtHolder.setInt(2, acc.getAccNumber());
        stmtHolder.setDouble(3, acc.getBalance());
        stmtHolder.execute();
        
    }
}
