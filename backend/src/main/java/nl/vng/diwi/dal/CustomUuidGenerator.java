package nl.vng.diwi.dal;

import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.UUIDUtil;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import java.util.UUID;

public class CustomUuidGenerator implements IdentifierGenerator {

    @Override
    public UUID generate(SharedSessionContractImplementor session, Object object) {
        byte[] uuid = UUIDUtil.asByteArray(Generators.timeBasedEpochGenerator().generate()); //UUID v7
        // @TODO: add code to inject the instanceID in the UUID here...
        return UUIDUtil.uuid(uuid);
    }

    public static UUID generateUUIDv7() {
        return Generators.timeBasedEpochGenerator().generate();
    }
}
