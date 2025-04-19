package it.mauluk92.jdbc.c3;

import it.mauluk92.jdbc.testutils.ConnectionParameterResolver;
import it.mauluk92.jdbc.testutils.SqlCallback;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * This class contains tests to validate rules about
 * making batch updates with JDBC API
 */
@ExtendWith({ConnectionParameterResolver.class, SqlCallback.class})
public class MakingBatchUpdatesTest {
}
