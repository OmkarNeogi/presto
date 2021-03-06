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
package io.prestosql.plugin.prometheus;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import io.prestosql.spi.PrestoException;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static io.prestosql.plugin.prometheus.PrometheusErrorCode.PROMETHEUS_UNKNOWN_ERROR;

public class PrometheusTimestampDeserializer
        extends JsonDeserializer<Timestamp>
{
    @Override
    public Timestamp deserialize(JsonParser jp, DeserializationContext context)
            throws IOException, JsonProcessingException
    {
        String timestamp = jp.getText().trim();
        try {
            return decimalEpochTimestampToSQLTimestamp(timestamp);
        }
        catch (NumberFormatException e) {
            throw new PrestoException(PROMETHEUS_UNKNOWN_ERROR, "unable to deserialize timestamp: " + e.getMessage());
        }
    }

    static Timestamp decimalEpochTimestampToSQLTimestamp(String timestamp)
    {
        long promTimestampMillis = (long) (Double.parseDouble(timestamp) * 1000);
        ZonedDateTime zonedDateTimeFromPrometheusDecimalTimestamp = Instant.ofEpochMilli(promTimestampMillis).atZone(ZoneId.systemDefault());
        return new Timestamp(zonedDateTimeFromPrometheusDecimalTimestamp.toInstant().toEpochMilli());
    }
}
