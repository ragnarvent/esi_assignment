package rentit.com.sales.domain.model;

import java.time.LocalDate;

import com.mysema.query.jpa.JPASubQuery;
import com.mysema.query.types.expr.BooleanExpression;

import rentit.com.common.domain.model.BusinessPeriod;
import rentit.com.inventory.domain.model.PlantInvItem.EquipmentCondition;
import rentit.com.inventory.domain.model.QPlantInvEntry;
import rentit.com.inventory.domain.model.QPlantInvItem;
import rentit.com.inventory.domain.model.QPlantReservation;
import rentit.com.maintenance.domain.model.QMaintenancePlan;

public class InventorySpecifications {
	private static final QPlantInvEntry plantEntry = QPlantInvEntry.plantInvEntry;
	private static final QPlantInvItem plantItem = QPlantInvItem.plantInvItem;
	private static final QPlantReservation reservation = QPlantReservation.plantReservation;
	private static final QMaintenancePlan maintPlan = QMaintenancePlan.maintenancePlan;

	public static BooleanExpression isAvailableFor(BusinessPeriod period) {
		return plantEntry.id.in(new JPASubQuery().from(plantItem)
                .where(plantInvItemIsAvailableFor(period))
                .list(plantItem.plantInfo.id));
	}
	
	public static BooleanExpression plantInvItemIsAvailableFor(BusinessPeriod period) {
        return plantItem.serialNumber.notIn(new JPASubQuery()
                .from(reservation)
                .where(reservation.rentalPeriod.endDate.goe(period.getStartDate()),
                        reservation.rentalPeriod.startDate.loe(period.getEndDate()))
                .list(reservation.plantItemId));
    }

	public static BooleanExpression nameContains(String keyword) {
		return plantEntry.name.lower().contains(keyword.toLowerCase());
	}
	
	public static BooleanExpression isServicable(){
		return plantItem.condition.eq(EquipmentCondition.SERVICEABLE);
	}
	
	public static BooleanExpression isServicableWithId(String id){
		return plantItem.serialNumber.eq(id).and(isServicable());
	}
	
	public static BooleanExpression isRelaxedServicableWithId(String id, BusinessPeriod period){
		BooleanExpression expr = plantItem.serialNumber.eq(id);
		if(period.getStartDate().isAfter(LocalDate.now().plusWeeks(3))){
			return expr.and(plantItem.serialNumber.notIn(
					new JPASubQuery().from(reservation).leftJoin(maintPlan).on(reservation.maintPlanId.eq(maintPlan.id))
						.where(maintPlan.tasks.any().schedule.startDate.before(period.getStartDate().minusWeeks(1))).list(reservation.plantItemId)
					).or(isServicable()));
		}
		return expr.and(isServicable());
	}
}


