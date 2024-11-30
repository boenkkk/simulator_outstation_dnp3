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

        for (int i = 0; i < 24; i++) {
            // TODO: NOTE BINARY_OUTPUT_STATUS(2)
            // 0 : CB Close / Open
            // 1 : Tap Changer Value Up
            // 2 : Tap Changer Value Down
            // 3 : Tap Changer Auto / Manual
            // 4 : Tap Changer Local / Remote
            // 5 : VR Auto / Manual
            // 6 : VR Value Down / Up
            // 7 : P  Auto / Manual
            // 8 : P  Value Down / Up
            // 9 : Q  Auto / Manual
            // 10 : Q  Value Down / Up
            // 11 : PF Auto / Manual
            // 12 : PF Value Down / Up
            // 13 : VS Auto / Manual
            // 14 : VS Value Down / Up
            // 15 : VT Auto / Manual
            // 16 : VT Value Down / Up
            // 17: F  Auto / Manual
            // 18: F  Value Down / Up
            // 19: IR Auto / Manual
            // 20: IR Value Down / Up
            // 21: IS Auto / Manual
            // 22: IS Value Down / Up
            // 23: IT Auto / Manual
            // 24: IT Value Down / Up
            BinaryOutputStatusConfig binaryOutputStatusConfig = new BinaryOutputStatusConfig()
                .withStaticVariation(StaticBinaryOutputStatusVariation.GROUP10_VAR2)
                .withEventVariation(EventBinaryOutputStatusVariation.GROUP11_VAR2);
            db.addBinaryOutputStatus(ushort(i), EventClass.CLASS1, binaryOutputStatusConfig);

            BinaryOutputStatus binaryOutputStatus = new BinaryOutputStatus(ushort(i), false, flags, timeNow);
            db.updateBinaryOutputStatus(binaryOutputStatus, updateOptions);
        }

        for (int i = 0; i < 13; i++){
            // TODO: NOTE BINARY_INPUT(1)
            // 0 : CB Local / Remote
            // 1 : CB Close / Open
            // 2 : Tap Changer Auto / Manual
            // 3 : Tap Changer Local / Remote
            // 4 : VR Auto / Manual
            // 5 : P  Auto / Manual
            // 6 : Q  Auto / Manual
            // 7 : PF Auto / Manual
            // 8 : VS Auto / Manual
            // 9 : VT Auto / Manual
            // 10: F  Auto / Manual
            // 11: IR Auto / Manual
            // 12: IS Auto / Manual
            // 13: IT Auto / Manual
            BinaryInputConfig binaryInputConfig = new BinaryInputConfig()
                .withStaticVariation(StaticBinaryInputVariation.GROUP1_VAR2)
                .withEventVariation(EventBinaryInputVariation.GROUP2_VAR2);
            db.addBinaryInput(ushort(i), EventClass.CLASS1, binaryInputConfig);

            BinaryInput binaryInput = new BinaryInput(ushort(i), false, flags, timeNow);
            db.updateBinaryInput(binaryInput, updateOptions);
        }

        for (int i = 0; i < 10; i++) {
            // TODO: NOTE ANALOG_INPUT(3)
            // 0 : Tap Changer 0 - 32
            // 1 : VR 309 sd 330
            // 2 : P 300 sd 400
            // 3 : Q 500 - 600
            // 4 : PF 0,000-1
            // 5 : VS 309 sd 330
            // 6 : VT 309 sd 330
            // 7 : F 49.5 - 51.5
            // 8 : IR 270 - 289
            // 9 : IS 270 - 289
            // 10: IT 270 - 289
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
