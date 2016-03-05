DROP SEQUENCE IF EXISTS PlantIDSequence;
DROP SEQUENCE IF EXISTS PlantReservationSequence;
DROP SEQUENCE IF EXISTS PurchaseOrderIDSequence;

CREATE SEQUENCE PlantIDSequence START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE PlantReservationSequence START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE PurchaseOrderIDSequence START WITH 1 INCREMENT BY 1;
