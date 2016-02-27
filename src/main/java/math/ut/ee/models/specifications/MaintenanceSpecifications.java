package math.ut.ee.models.specifications;

import java.time.Year;

import com.mysema.query.types.expr.BooleanExpression;

import math.ut.ee.models.QMaintenancePlan;

public class MaintenanceSpecifications {
	private static final QMaintenancePlan maintEntry = QMaintenancePlan.maintenancePlan;
	
	public static BooleanExpression isWithinPastYears(int pastYears){
		return maintEntry.yearOfAction.gt( Year.now().getValue() - pastYears );
	}
	
}
