//
// MIT License
//
// Copyright (c) 2022 Joel Strasser
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.
//
package at.joestr.postbox.configuration;

import at.joestr.postbox.configuration.DatabaseModels.PostBoxModel;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * @author joestr
 */
public class DatabaseConfiguration {

  private static DatabaseConfiguration instance;
  private static final Logger LOG = Logger.getLogger(AppConfiguration.class.getSimpleName());

  private ConnectionSource connectionSource = null;
  private Dao<PostBoxModel, String> postBoxDao = null;

  private DatabaseConfiguration(String jdbcUri) throws ClassNotFoundException, SQLException {
    Class.forName("org.sqlite.JDBC");

    this.connectionSource = new JdbcConnectionSource(jdbcUri);

    this.registerDaos(connectionSource);
    this.checkTables(connectionSource);
  }

  private void registerDaos(ConnectionSource connectionSource) throws SQLException {
    this.postBoxDao = DaoManager.createDao(connectionSource, PostBoxModel.class);
  }

  private void checkTables(ConnectionSource connectionSource) throws SQLException {
    TableUtils.createTableIfNotExists(connectionSource, PostBoxModel.class);
  }

  public static DatabaseConfiguration getInstance(String jdbcUri)
    throws ClassNotFoundException, SQLException {
    if (instance != null) {
      throw new RuntimeException("This class has already been instantiated.");
    }

    instance = new DatabaseConfiguration(jdbcUri);

    return instance;
  }

  public static DatabaseConfiguration getInstance() {
    if (instance == null) {
      throw new RuntimeException("This class has not been initialized yet.");
    }

    return instance;
  }

  public ConnectionSource getConnectionSource() {
    return this.connectionSource;
  }

  public Dao<PostBoxModel, String> getPostBoxDao() {
    return this.postBoxDao;
  }
}
