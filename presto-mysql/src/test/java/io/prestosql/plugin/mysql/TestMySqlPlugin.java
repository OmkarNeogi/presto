/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.prestosql.plugin.mysql;

import com.google.common.collect.ImmutableMap;
import io.prestosql.spi.Plugin;
import io.prestosql.spi.connector.ConnectorFactory;
import io.prestosql.testing.TestingConnectorContext;
import org.testng.annotations.Test;

import static com.google.common.collect.Iterables.getOnlyElement;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class TestMySqlPlugin
{
    @Test
    public void testCreateConnector()
    {
        createMySqlPlugin("jdbc:mysql://test");

        assertThatThrownBy(() -> createMySqlPlugin("test"))
                .hasMessageContaining("Invalid JDBC URL for MySQL connector");

        assertThatThrownBy(() -> createMySqlPlugin("jdbc:mysql://test/abc"))
                .hasMessageContaining("Database (catalog) must not be specified in JDBC URL for MySQL connector");
    }

    private static void createMySqlPlugin(String url)
    {
        Plugin plugin = new MySqlPlugin();
        ConnectorFactory factory = getOnlyElement(plugin.getConnectorFactories());
        factory.create("test", ImmutableMap.of("connection-url", url), new TestingConnectorContext());
    }
}
