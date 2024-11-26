package dev.boenkkk.simulator_outstation_dnp3.config;

import dev.boenkkk.simulator_outstation_dnp3.util.TimeUtil;
import io.stepfunc.dnp3.*;
import lombok.extern.slf4j.Slf4j;

import static org.joou.Unsigned.ubyte;
import static org.joou.Unsigned.ushort;

@Slf4j
public class DatabaseConfigImpl {

    // ANCHOR: database_init_function
    public static void initializeDatabaseDefault(Database db) {
        Flags flags = new Flags(Flag.ONLINE);
        Timestamp timeNow = TimeUtil.now();
        UpdateOptions updateOptions = UpdateOptions.detectEvent();

        // define device attributes made available to the master
        db.defineStringAttr(ubyte(0), false, AttributeVariations.USER_ASSIGNED_OWNER_NAME, "BOENKKK.DEV");
        db.defineStringAttr(ubyte(0), true, AttributeVariations.PRODUCT_NAME_AND_MODEL, "SIMULATOR DNP3 OUTSTATION");

        for (int i= 0; i< 3; i++){
            // TODO: NOTE
            // 0: Local / Remote
            // 1: CB Open / Close
            BinaryInputConfig binaryInputConfig = new BinaryInputConfig()
                .withStaticVariation(StaticBinaryInputVariation.GROUP1_VAR2)
                .withEventVariation(EventBinaryInputVariation.GROUP2_VAR2);
            db.addBinaryInput(ushort(i), EventClass.CLASS1, binaryInputConfig);

            BinaryInput binaryInput = new BinaryInput(ushort(i), false, flags, timeNow);
            db.updateBinaryInput(binaryInput, updateOptions);
        }

        for (int i = 0; i < 2; i++) {
            // TODO: NOTE
            // 0: AI0 Up
            // 1: AI0 Down
            BinaryOutputStatusConfig binaryOutputStatusConfig = new BinaryOutputStatusConfig()
                .withStaticVariation(StaticBinaryOutputStatusVariation.GROUP10_VAR2)
                .withEventVariation(EventBinaryOutputStatusVariation.GROUP11_VAR2);
            db.addBinaryOutputStatus(ushort(i), EventClass.CLASS1, binaryOutputStatusConfig);

            BinaryOutputStatus binaryOutputStatus = new BinaryOutputStatus(ushort(i), false, flags, timeNow);
            db.updateBinaryOutputStatus(binaryOutputStatus, updateOptions);
        }

        for (int i = 0; i < 1; i++) {
            // TODO: NOTE
            // 0: Simulate Tap Changer
            AnalogInputConfig analogInputConfig = new AnalogInputConfig()
                .withStaticVariation(StaticAnalogInputVariation.GROUP30_VAR4)
                .withEventVariation(EventAnalogInputVariation.GROUP32_VAR4);
            db.addAnalogInput(ushort(i), EventClass.CLASS1, analogInputConfig);

            AnalogInput analogInput = new AnalogInput(ushort(i), 0.0, flags, timeNow);
            db.updateAnalogInput(analogInput, UpdateOptions.detectEvent());
        }
    }
    // ANCHOR_END: database_init_function
}
