import java.sql.*;
import java.util.*;
import java.io.IOException;
import java.nio.file.*;

public class FortniteSQL implements AutoCloseable {

    private static final String DB_NAME = "crk1120_Fortnite3";
    private static final String DB_USER = "token_a9fa";
    private static final String DB_PASSWORD = "Mc2NJHgdBlyVNDkV";		
    
    private static final String SQL_QUERY_UPDATE_SKIN_TO_FAVORITE = 
            "UPDATE Skin \n"
            + "SET fav = 'favorited'\n"
            + "WHERE id = ?";
    
    private static final String SQL_QUERY_UPDATE_SKIN_FROM_FAVORITE = 
            "UPDATE Skin \n"
            + "SET fav = 'no'\n"
            + "WHERE id = ?";
    
    private static final String SQL_QUERY_UPDATE_BACKBLING_TO_FAVORITE = 
            "UPDATE Backbling \n"
            + "SET fav = 'favorited'\n"
            + "WHERE id = ?";
    
    private static final String SQL_QUERY_UPDATE_BACKBLING_FROM_FAVORITE = 
            "UPDATE Backbling \n"
            + "SET fav = 'no'\n"
            + "WHERE id = ?";
    
    private static final String SQL_QUERY_UPDATE_SKIN_TO_OWNED = 
            "UPDATE Skin \n"
            + "SET owns = 'owned'\n"
            + "WHERE id = ?";       
    
    private static final String SQL_QUERY_UPDATE_SKIN_FROM_OWNED = 
            "UPDATE Skin \n"
            + "SET owns = 'no'\n"
            + "WHERE id = ?";   
    
    private static final String SQL_QUERY_UPDATE_BACKBLING_TO_OWNED = 
            "UPDATE Backbling \n"
            + "SET owns = 'owned'\n"
            + "WHERE id = ?";       
    
    private static final String SQL_QUERY_UPDATE_BACKBLING_FROM_OWNED = 
            "UPDATE Backbling \n"
            + "SET owns = 'no'\n"
            + "WHERE id = ?"; 
    
    private static final String SQL_QUERY_ADD_SKIN = 
    		"INSERT INTO Skin (name, price, rarity, fav, owns, battlePassId) VALUES \n"
    		+ "(?, ?, ?, ?, ?, ?)";
    
    private static final String SQL_QUERY_ADD_BACKBLING = 
    		"INSERT INTO Backbling (name, price, rarity, fav, owns, battlePassId) VALUES \n"
    		+ "(?, ?, ?, ?, ?, ?)";
    
    private static final String SQL_QUERY_DELETE_SKIN = 
    		"DELETE FROM Skin \n"
    		+ "WHERE id = ?";
    
    private static final String SQL_QUERY_DELETE_BACKBLING = 
    		"DELETE FROM Backbling \n"
    		+ "WHERE id = ?";
    
    private static final String SQL_QUERY_SHOW_FAV_SKIN = 
    		"SELECT * \n"
    		+ "FROM Skin \n"
    		+ "WHERE fav = 'favorited'";
    
    private static final String SQL_QUERY_SHOW_UN_FAV_SKIN = 
    		"SELECT * \n"
    		+ "FROM Skin \n"
    		+ "WHERE fav = 'no'";
    
    private static final String SQL_QUERY_SHOW_OWNED_SKIN = 
    		"SELECT * \n"
    		+ "FROM Skin \n"
    		+ "WHERE owns = 'owned'";
    
    private static final String SQL_QUERY_SHOW_UN_OWNED_SKIN = 
    		"SELECT * \n"
    		+ "FROM Skin \n"
    		+ "WHERE owns = 'no'";
    
    private static final String SQL_QUERY_SHOW_FAV_BACKBLING = 
    		"SELECT * \n"
    		+ "FROM Backbling \n"
    		+ "WHERE fav = 'favorited'";
    
    private static final String SQL_QUERY_SHOW_UN_FAV_BACKBLING = 
    		"SELECT * \n"
    		+ "FROM Backbling \n"
    		+ "WHERE fav = 'no'";
    
    private static final String SQL_QUERY_SHOW_OWNED_BACKBLING = 
    		"SELECT * \n"
    		+ "FROM Backbling \n"
    		+ "WHERE owns = 'owned'";
    
    private static final String SQL_QUERY_SHOW_UN_OWNED_BACKBLING = 
    		"SELECT * \n"
    		+ "FROM Backbling \n"
    		+ "WHERE owns = 'no'";
    
    private static final String SQL_QUERY_SHOW_MAX_SKIN = 
    		"SELECT name, MAX(price) AS price, owns, rarity \n"
    		+ "FROM Skin \n"
    		+ "WHERE owns = 'owned'\n"
    		+ "GROUP BY rarity";
    
    private static final String SQL_QUERY_SHOW_MAX_BACKBLING = 
    		"SELECT name, MAX(price) AS price, owns, rarity \n"
    		+ "FROM Backbling \n"
    		+ "WHERE owns = 'owned'\n"
    		+ "GROUP BY rarity";
    
    private static final String SQL_QUERY_SHOW_ITEM_SET = 
    		"SELECT * FROM ItemSet";
    
    private static final String SQL_QUERY_SHOW_IN_SET = 
    		"SELECT ItemSet.name AS ItemSet, Skin.name AS Skin, Backbling.name AS Backbling\n"
    		+ " FROM ItemSet\n"
    		+ " INNER JOIN BackblingInSet ON BackblingInSet.setId = ItemSet.id\n"
    		+ " INNER JOIN Backbling ON Backbling.id = BackblingInSet.backblingId\n"
    		+ " INNER JOIN (\n"
    		+ " SELECT Skin.name AS name\n"
    		+ " FROM Skin\n"
    		+ "    INNER JOIN SkinInSet ON SkinInSet.skinId = Skin.Id\n"
    		+ "    WHERE SkinInSet.setId = ?\n"
    		+ " ) AS Skin\n"
    		+ " WHERE BackblingInSet.setId = ?";
    
    // Declare one of these for every query your program will use.
    private PreparedStatement updateSkinToFav;
    private PreparedStatement updateSkinFromFav;
    private PreparedStatement updateBackblingToFav;
    private PreparedStatement updateBackblingFromFav;
    private PreparedStatement updateSkinToOwned;
    private PreparedStatement updateSkinFromOwned;
    private PreparedStatement updateBackblingToOwned;
    private PreparedStatement updateBackblingFromOwned;
    private PreparedStatement addSkin;
    private PreparedStatement deleteSkin;
    private PreparedStatement addBackbling;
    private PreparedStatement deleteBackbling;
    private PreparedStatement showFavSkin;
    private PreparedStatement showUnFavSkin;
    private PreparedStatement showOwnedSkin;
    private PreparedStatement showUnOwnedSkin;
    private PreparedStatement showFavBackbling;
    private PreparedStatement showUnFavBackbling;
    private PreparedStatement showOwnedBackbling;
    private PreparedStatement showUnOwnedBackbling;
    private PreparedStatement showMaxSkin;
    private PreparedStatement showMaxBackbling;
    private PreparedStatement showSet;
    private PreparedStatement showInSet;


    
    // Connection information to use
    private final String dbHost;
    private final int dbPort;
    private final String dbName;
    private final String dbUser, dbPassword;

    // The database connection
    private Connection connection;

    public FortniteSQL(String dbHost, int dbPort, String dbName,
            String dbUser, String dbPassword) throws SQLException {
        this.dbHost = dbHost;
        this.dbPort = dbPort;
        this.dbName = dbName;
        this.dbUser = dbUser;
        this.dbPassword = dbPassword;

        connect();
    }

    private void connect() throws SQLException {
        // URL for connecting to the database: includes host, port, database name,
        // user, password
        final String url = String.format("jdbc:mysql://%s:%d/%s?user=%s&password=%s",
                dbHost, dbPort, dbName,
                dbUser, dbPassword
        );

        // Attempt to connect, returning a Connection object if successful
        this.connection = DriverManager.getConnection(url);

        // Prepare the statements (queries) that we will execute
        // One of these lines for each query your program will use
        this.updateSkinToFav = this.connection.prepareStatement(SQL_QUERY_UPDATE_SKIN_TO_FAVORITE);
        this.updateSkinFromFav = this.connection.prepareStatement(SQL_QUERY_UPDATE_SKIN_FROM_FAVORITE);
        this.updateBackblingToFav = this.connection.prepareStatement(SQL_QUERY_UPDATE_BACKBLING_TO_FAVORITE);
        this.updateBackblingFromFav = this.connection.prepareStatement(SQL_QUERY_UPDATE_BACKBLING_FROM_FAVORITE);
        this.updateSkinToOwned = this.connection.prepareStatement(SQL_QUERY_UPDATE_SKIN_TO_OWNED);
        this.updateSkinFromOwned = this.connection.prepareStatement(SQL_QUERY_UPDATE_SKIN_FROM_OWNED);
        this.updateBackblingToOwned = this.connection.prepareStatement(SQL_QUERY_UPDATE_BACKBLING_TO_OWNED);
        this.updateBackblingFromOwned = this.connection.prepareStatement(SQL_QUERY_UPDATE_BACKBLING_FROM_OWNED);
        this.addSkin = this.connection.prepareStatement(SQL_QUERY_ADD_SKIN);
        this.deleteSkin = this.connection.prepareStatement(SQL_QUERY_DELETE_SKIN);
        this.addBackbling = this.connection.prepareStatement(SQL_QUERY_ADD_BACKBLING);
        this.deleteBackbling = this.connection.prepareStatement(SQL_QUERY_DELETE_BACKBLING);
        this.showFavSkin = this.connection.prepareStatement(SQL_QUERY_SHOW_FAV_SKIN);
        this.showUnFavSkin = this.connection.prepareStatement(SQL_QUERY_SHOW_UN_FAV_SKIN);
        this.showOwnedSkin = this.connection.prepareStatement(SQL_QUERY_SHOW_OWNED_SKIN);
        this.showUnOwnedSkin = this.connection.prepareStatement(SQL_QUERY_SHOW_UN_OWNED_SKIN);
        this.showFavBackbling = this.connection.prepareStatement(SQL_QUERY_SHOW_FAV_BACKBLING);
        this.showUnFavBackbling = this.connection.prepareStatement(SQL_QUERY_SHOW_UN_FAV_BACKBLING);
        this.showOwnedBackbling = this.connection.prepareStatement(SQL_QUERY_SHOW_OWNED_BACKBLING);
        this.showUnOwnedBackbling = this.connection.prepareStatement(SQL_QUERY_SHOW_UN_OWNED_BACKBLING);
        this.showMaxSkin = this.connection.prepareStatement(SQL_QUERY_SHOW_MAX_SKIN);
        this.showMaxBackbling = this.connection.prepareStatement(SQL_QUERY_SHOW_MAX_BACKBLING);
        this.showSet = this.connection.prepareStatement(SQL_QUERY_SHOW_ITEM_SET);
        this.showInSet = this.connection.prepareStatement(SQL_QUERY_SHOW_IN_SET);
    }


    public void runApp() throws SQLException {
        Scanner in = new Scanner(System.in);
        while (true) {
        	System.out.println("Welcome to your fortnite locker room!\n");
        	
        	System.out.println("Here are your favorite skins: ");
            showFavSkin();
            
            System.out.println("Here are your favorite backblings: ");
            showFavBackbling();
            
            System.out.print("\nWhat would you like to do? \n 1) = Update favorites \n 2) = Update ownership \n 3) = Show all owned skins/backblings \n 4) = Show all un-owned skins/backblings \n 5) = Show all un-favorited skins/backblings \n 6) = Show most valuable skins/backblings \n 7) = Add skins/backblings \n 8) = Delete skins/backbling \n 9) Show all sets \n 10) Show all sets \n(or hit Enter to quit): ");
            String line = in.nextLine();
            if (line.isBlank())
                break;
            
            
            else if (line.equals("1")) {
            System.out.println("You chose to update your favorites list! Are you updating skins or backblings? (Enter '1' for skins or '2' for backblings:");
            	String updateFavSkBa = in.nextLine();
            	if (updateFavSkBa.equals("1")) {
            		System.out.println("You are updating skins! Would you like the skin to become a favorite or revoke it's favorite status? (Enter '1' for becoming favorite or '2' for revoking favorite status: ");
            			String updateSkinFavToFr = in.nextLine();
            			if (updateSkinFavToFr.equals("1")) {
            				System.out.println("What is the id of the skin you'd like to become a favorite?");
            					String updateSkinFavToId = in.nextLine();
            					updateSkinToFav(updateSkinFavToId);
            			}
            			else {
            				System.out.println("What is the id of the skin you'd like to revoke status from?");
        					String updateSkinFavFrId = in.nextLine();
        					updateSkinFromFav(updateSkinFavFrId);
            			}
            	}
            	else {
            		System.out.println("You are updating backblings! Would you like the backbling to become a favorite or revoke it's favorite status? (Enter '1' for becoming favorite or '2' for revoking favorite status: ");
        			String updateBackFavToFr = in.nextLine();
        			if (updateBackFavToFr.equals("1")) {
        				System.out.println("What is the id of the backbling you'd like to become a favorite?");
        					String updateBackFavToId = in.nextLine();
        					updateBackblingToFav(updateBackFavToId);
        			}
        			else {
        				System.out.println("What is the id of the backbling you'd like to revoke status from?");
    					String updateBackFavFrId = in.nextLine();
    					updateBackblingFromFav(updateBackFavFrId);
        			}
            	}
            }
            
            
            
            else if(line.equals("2")) {
                System.out.println("You chose to update the ownership of an item! Are you updating a skin or a backbling? (Enter '1' for skin or '2' for backbling:");
            	String updateOwnSkBa = in.nextLine();
            	if (updateOwnSkBa.equals("1")) {
            		System.out.println("You are updating skins! Do you now own this skin or are revoke it's ownership? (Enter '1' for owned or '2' for revoking ownership: ");
            			String updateSkinOwnToFr = in.nextLine();
            			if (updateSkinOwnToFr.equals("1")) {
            				System.out.println("What is the id of the skin you now own?");
            					String updateSkinOwnToId = in.nextLine();
            					updateSkinToOwned(updateSkinOwnToId);
            			}
            			else {
            				System.out.println("What is the id of the skin you'd like to revoke ownership from?");
        					String updateSkinOwnFrId = in.nextLine();
        					updateSkinFromFav(updateSkinOwnFrId);
            			}
            	}
            	
            	else {
            		System.out.println("You are updating backblings! Do you now own this skin or are revoke it's ownership? (Enter '1' for owned or '2' for revoking ownership: ");
        			String updateBackOwnToFr = in.nextLine();
        			if (updateBackOwnToFr.equals("1")) {
        				System.out.println("What is the id of the backbling you now own?");
        					String updateBackOwnToId = in.nextLine();
        					updateBackblingToOwned(updateBackOwnToId);
        			}
        			else {
        				System.out.println("What is the id of the backbling you'd like to revoke ownership from?");
    					String updateBackOwnFrId = in.nextLine();
    					updateBackblingFromOwned(updateBackOwnFrId);
        			}
            	}
            }
            
            
            
            else if (line.equals("3")) {
            System.out.println("You chose to show all owned skins/backblings! Do you want to see all of your owned skins or backblings? (Enter '1' for skins or '2' for backblings:");
            	String showOwner = in.nextLine();
            	if (showOwner.equals("1")) {
            		showOwnedSkin();
            	}
            	else {
            		showOwnedBackbling();
            	}
            }
            
            
            
            else if (line.equals("4")) {
            System.out.println("You chose to show all unowned skins/backblings! Do you want to see all of your unowned skins or backblings? (Enter '1' for skins or '2' for backblings:");
            	String showUnOwner = in.nextLine();
            	if (showUnOwner.equals("1")) {
            		showUnOwnedSkin();
            	}
            	else {
            		showUnOwnedBackbling();
            	}
            }
            
            
            
            else if (line.equals("5")) {
            System.out.println("You chose to show all un-favorited skins/backblings! Do you want to see all of your un-favorited skins or backblings? (Enter '1' for skins or '2' for backblings:");
            	String showUnFav = in.nextLine();
            	if (showUnFav.equals("1")) {
            		showUnFavSkin();
            	}
            	else {
            		showUnFavBackbling();
            	}
            }
            
            
            
            else if (line.equals("6")) {
                System.out.println("You chose to show your most valuable skins/backblings! Do you want to see your most valuable skins or backblings? (Enter '1' for skins or '2' for backblings:");
                	String showValue = in.nextLine();
                	if (showValue.equals("1")) {
                		showMaxSkin();
                	}
                	else {
                		showMaxBackbling();
                	}
                }
            
            
            
            else if (line.equals("7")) {
            System.out.println("You chose to add a skin/backbling! Are you adding a skin or a backbling? (Enter '1' for skins or '2' for backblings:");
            	String addSkin = in.nextLine();
            	if (addSkin.equals("1")) {
            		System.out.println("You are adding a skin! What is the name of the skin?");
            			String addSkinName = in.nextLine();
            		System.out.println("What is the price of the skin?");	
            			String addSkinPrice = in.nextLine();
            		System.out.println("What is the rarity of the skin?");
            			String addSkinRare = in.nextLine();
            		System.out.println("What is the favorite status of the skin?");
            			String addSkinFav = in.nextLine();
            		System.out.println("What is the ownership status of the skin?");
            			String addSkinOwns = in.nextLine();
            		System.out.println("What season was the skin introduced?");
            			String addSkinSeason = in.nextLine();
            			
            		addSkin(addSkinName, addSkinPrice, addSkinRare, addSkinFav, addSkinOwns, addSkinSeason);
            	}
            	else {
            	System.out.println("You are adding a Backbling! What is the name of the backbling?");
        			String addBackName = in.nextLine();
        		System.out.println("What is the price of the backbling?");	
        			String addBackPrice = in.nextLine();
        		System.out.println("What is the rarity of the backbling?");
        			String addBackRare = in.nextLine();
        		System.out.println("What is the favorite status of the backbling?");
        			String addBackFav = in.nextLine();
        		System.out.println("What is the ownership status of the backbling?");
        			String addBackOwns = in.nextLine();
        		System.out.println("What season was the backbling introduced?");
        			String addBackSeason = in.nextLine();
        			
        		addBackbling(addBackName, addBackPrice, addBackRare, addBackFav, addBackOwns, addBackSeason);
            	}
            }
            
            
            
            else if (line.equals("8")) {
                System.out.println("You chose to delete skins/backblings! Do you want to delete skins or backblings? (Enter '1' for skins or '2' for backblings:");
                	String deleter = in.nextLine();
                	if (deleter.equals("1")) {
                		System.out.println("What is the id of the skin you want to delete?");
                		String deleterSkinId = in.nextLine();
                		deleteSkin(deleterSkinId);
                	}
                	else {
                		System.out.println("What is the id of the backbling you want to delete?");
                		String deleterBackId = in.nextLine();
                		deleteBackbling(deleterBackId);
                	}
                }
            
            
            else if (line.equals("9")) {
            System.out.println("You chose to show all sets! Here they are: ");
            	showSet();
            }
            
            
            else if (line.equals("10")) {
            System.out.println("You chose to show all the items in a set! What is the id of the set you want to see all of the items in: ");
            	String showInSet = in.nextLine();
            	showInSet(showInSet);
            }
            
        }
    }
    
    public void updateSkinToFav(String id) throws SQLException {
    	updateSkinToFav.setString(1, id);
        updateSkinToFav.execute();
    }
    
    public void updateSkinFromFav(String id) throws SQLException {
    	updateSkinFromFav.setString(1, id);
        updateSkinFromFav.execute();
    }
    
    public void updateBackblingToFav(String id) throws SQLException {
    	updateBackblingToFav.setString(1, id);
        updateBackblingToFav.execute();
    }
    
    public void updateBackblingFromFav(String id) throws SQLException {
    	updateBackblingFromFav.setString(1, id);
        updateBackblingFromFav.execute();
    }
    
    public void updateSkinToOwned(String id) throws SQLException {
    	updateSkinToOwned.setString(1, id);
        updateSkinToOwned.execute();
    }
    
    public void updateSkinFromOwned(String id) throws SQLException {
    	updateSkinFromOwned.setString(1, id);
        updateSkinFromOwned.execute();
    }
    
    public void updateBackblingToOwned(String id) throws SQLException {
    	updateBackblingToOwned.setString(1, id);
        updateBackblingToOwned.execute();
    }
    
    public void updateBackblingFromOwned(String id) throws SQLException {
    	updateBackblingFromOwned.setString(1, id);
        updateBackblingFromOwned.execute();
    }
    
    public void addSkin(String name, String price, String rarity, String fav, String owns, String battlePassId) throws SQLException {
        addSkin.setString(1, name);
        addSkin.setString(2, price);
        addSkin.setString(3, rarity);
        addSkin.setString(4, fav);
        addSkin.setString(5, owns);
        addSkin.setString(6, battlePassId);
        addSkin.execute();
    }
    
    public void deleteSkin(String id) throws SQLException {
    	deleteSkin.setString(1, id);
        deleteSkin.execute();
    }
    
    public void addBackbling(String name, String price, String rarity, String fav, String owns, String battlePassId) throws SQLException {
        addBackbling.setString(1, name);
        addBackbling.setString(2, price);
        addBackbling.setString(3, rarity);
        addBackbling.setString(4, fav);
        addBackbling.setString(5, owns);
        addBackbling.setString(6, battlePassId);
        addBackbling.execute();
    }
    
    public void deleteBackbling(String id) throws SQLException {
    	deleteBackbling.setString(1, id);
        deleteBackbling.execute();
    }
    
    public void showFavSkin() throws SQLException {
        ResultSet results = showFavSkin.executeQuery();

        while (results.next()) {
        	String id = results.getString("id");
        	String name = results.getString("name");
            int price = results.getInt("price");
            String rarity = results.getString("rarity");
            int battlePassId = results.getInt("battlePassId");
            System.out.println("Id #" + id + ")" + name + ", " + price + "(In v-bucks), " + rarity + ", "  + "From season: " + battlePassId + "\n");
        }
    }
    
    public void showUnFavSkin() throws SQLException {
        ResultSet results = showUnFavSkin.executeQuery();

        while (results.next()) {
        	String id = results.getString("id");
        	String name = results.getString("name");
            int price = results.getInt("price");
            String rarity = results.getString("rarity");
            int battlePassId = results.getInt("battlePassId");
            System.out.println("Id #" + id + ")" + name + ", " + price + "(In v-bucks), " + rarity + ", "  + "From season: " + battlePassId + "\n");
        }
    }
    
    public void showOwnedSkin() throws SQLException {
        ResultSet results = showOwnedSkin.executeQuery();

        // Iterate over each row of the results
        while (results.next()) {
        	String id = results.getString("id");
        	String name = results.getString("name");
            int price = results.getInt("price");
            String rarity = results.getString("rarity");
            String fav = results.getString("fav");
            int battlePassId = results.getInt("battlePassId");
            System.out.println("Id #" + id + ")" + name + ", " + price + "(In v-bucks), " + rarity + ", "  + "Favorite? " + fav + " From season: " + battlePassId + "\n");
        }
    }
    
    public void showUnOwnedSkin() throws SQLException {
        ResultSet results = showUnOwnedSkin.executeQuery();

        while (results.next()) {
        	String id = results.getString("id");
        	String name = results.getString("name");
            int price = results.getInt("price");
            String rarity = results.getString("rarity");
            int battlePassId = results.getInt("battlePassId");
            System.out.println("Id #" + id + ")" + name + ", " + price + "(In v-bucks), " + rarity + ", "  + "From season: " + battlePassId + "\n");
        }
    }
    
    public void showFavBackbling() throws SQLException {
        ResultSet results = showFavBackbling.executeQuery();

        while (results.next()) {
        	String id = results.getString("id");
        	String name = results.getString("name");
            int price = results.getInt("price");
            String rarity = results.getString("rarity");
            int battlePassId = results.getInt("battlePassId");
            System.out.println("Id #" + id + ")" + name + ", " + price + "(In v-bucks), " + rarity + ", "  + "From season: " + battlePassId + "\n");
        }
    }
    
    public void showUnFavBackbling() throws SQLException {
        ResultSet results = showUnFavBackbling.executeQuery();

        while (results.next()) {
        	String id = results.getString("id");
        	String name = results.getString("name");
            int price = results.getInt("price");
            String rarity = results.getString("rarity");
            int battlePassId = results.getInt("battlePassId");
            System.out.println("Id #" + id + ")" + name + ", " + price + "(In v-bucks), " + rarity + ", "  + "From season: " + battlePassId + "\n");
        }
    }
    
    public void showOwnedBackbling() throws SQLException {
        ResultSet results = showOwnedBackbling.executeQuery();

        while (results.next()) {
        	String id = results.getString("id");
        	String name = results.getString("name");
            int price = results.getInt("price");
            String rarity = results.getString("rarity");
            String fav = results.getString("fav");
            int battlePassId = results.getInt("battlePassId");
            System.out.println("Id #" + id + ")" + name + ", " + price + "(In v-bucks), " + rarity + ", "  + "Favorite? " + fav + " From season: " + battlePassId + "\n");
        }
    }
    
    public void showUnOwnedBackbling() throws SQLException {
        ResultSet results = showUnOwnedBackbling.executeQuery();

        while (results.next()) {
        	String id = results.getString("id");
        	String name = results.getString("name");
            int price = results.getInt("price");
            String rarity = results.getString("rarity");
            int battlePassId = results.getInt("battlePassId");
            System.out.println("Id #" + id + ")" + name + ", " + price + "(In v-bucks), " + rarity + ", "  + "From season: " + battlePassId + "\n");
        }
    }
   
    public void showMaxSkin() throws SQLException {
        ResultSet results = showMaxSkin.executeQuery();

        while (results.next()) {
            String name = results.getString("name");
            int price = results.getInt("price");
            String rarity = results.getString("rarity");
            System.out.println(rarity + " " + name + " " + price + "(In v-bucks)");
        }
    }
    
    public void showMaxBackbling() throws SQLException {
        ResultSet results = showMaxBackbling.executeQuery();

        while (results.next()) {
            String name = results.getString("name");
            int price = results.getInt("price");
            String rarity = results.getString("rarity");
            System.out.println(rarity + " " + name + " " + price + "(In v-bucks)");
        }
    }
    
    public void showSet() throws SQLException {
        ResultSet results = showSet.executeQuery();

        while (results.next()) {
        	String id = results.getString("id");
            String name = results.getString("name");
            System.out.println(id + " " + name);
        }
    }
    
    public void showInSet(String number) throws SQLException {
    	showInSet.setString(1, number);
    	showInSet.setString(2, number);
    	ResultSet results = showInSet.executeQuery();

        while (results.next()) {
        	String set = results.getString("ItemSet");
            String skin = results.getString("Skin");
            String back = results.getString("Backbling");
            System.out.println("Set Name: " + set + " Skin Name(s): " + skin + " Backbling Name(s): " + back);
        }
    }
    
    /**
     * Closes the connection to the database.
     */
    @Override
    public void close() throws SQLException {
        connection.close();
    }

    /**
     * Entry point of the application. Uses command-line parameters to override database
     * connection settings, then invokes runApp().
     */
    public static void main(String... args) {
        // Default connection parameters (can be overridden on command line)
        Map<String, String> params = new HashMap<>(Map.of(
            "dbname", "" + DB_NAME,
            "user", DB_USER,
            "password", DB_PASSWORD
        ));

        boolean printHelp = false;

        // Parse command-line arguments, overriding values in params
        for (int i = 0; i < args.length && !printHelp; ++i) {
            String arg = args[i];
            boolean isLast = (i + 1 == args.length);

            switch (arg) {
            case "-h":
            case "-help":
                printHelp = true;
                break;

            case "-dbname":
            case "-user":
            case "-password":
                if (isLast)
                    printHelp = true;
                else
                    params.put(arg.substring(1), args[++i]);
                break;

            default:
                System.err.println("Unrecognized option: " + arg);
                printHelp = true;
            }
        }

        // If help was requested, print it and exit
        if (printHelp) {
            printHelp();
            return;
        }

        // Connect to the database. This use of "try" ensures that the database connection
        // is closed, even if an exception occurs while running the app.
        try (DatabaseTunnel tunnel = new DatabaseTunnel();
             FortniteSQL app = new FortniteSQL(
                "localhost", tunnel.getForwardedPort(), params.get("dbname"),
                params.get("user"), params.get("password")
            )) {
            
            // Run the application
            try {
                app.runApp();
            } catch (SQLException ex) {
                System.err.println("\n\n=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
                System.err.println("SQL error when running database app!\n");
                ex.printStackTrace();
                System.err.println("\n\n=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
            }
        } catch (IOException ex) {
            System.err.println("Error setting up ssh tunnel.");
            ex.printStackTrace();
        } catch (SQLException ex) {
            System.err.println("Error communicating with the database (see full message below).");
            ex.printStackTrace();
            System.err.println("\nParameters used to connect to the database:");
            System.err.printf("\tSSH keyfile: %s\n\tDatabase name: %s\n\tUser: %s\n\tPassword: %s\n\n",
                    params.get("sshkeyfile"), params.get("dbname"),
                    params.get("user"), params.get("password")
            );
            System.err.println("(Is the MySQL connector .jar in the CLASSPATH?)");
            System.err.println("(Are the username and password correct?)");
        }
        
    }

    private static void printHelp() {
        System.out.println("Accepted command-line arguments:");
        System.out.println();
        System.out.println("\t-help, -h          display this help text");
        System.out.println("\t-dbname <text>     override name of database to connect to");
        System.out.printf( "\t                   (default: %s)\n", DB_NAME);
        System.out.println("\t-user <text>       override database user");
        System.out.printf( "\t                   (default: %s)\n", DB_USER);
        System.out.println("\t-password <text>   override database password");
        System.out.println();
    }
}


