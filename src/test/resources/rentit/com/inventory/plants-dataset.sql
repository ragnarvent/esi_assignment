insert into plant_inventory_entry (id, name, description, price)
    values (1, 'Mini excavator', '1.5 Tonne Mini excavator', 150);
insert into plant_inventory_entry (id, name, description, price)
    values (2, 'Mini excavator', '3 Tonne Mini excavator', 200);
insert into plant_inventory_entry (id, name, description, price)
    values (3, 'Midi excavator', '5 Tonne Midi excavator', 250);
insert into plant_inventory_entry (id, name, description, price)
    values (4, 'Midi excavator', '8 Tonne Midi excavator', 300);
insert into plant_inventory_entry (id, name, description, price)
    values (5, 'Maxi excavator', '15 Tonne Large excavator', 400);
insert into plant_inventory_entry (id, name, description, price)
    values (6, 'Maxi excavator', '20 Tonne Large excavator', 450);
insert into plant_inventory_entry (id, name, description, price)
    values (7, 'HS dumper', '1.5 Tonne Hi-Swivel Dumper', 150);
insert into plant_inventory_entry (id, name, description, price)
    values (8, 'FT dumper', '2 Tonne Front Tip Dumper', 180);
insert into plant_inventory_entry (id, name, description, price)
    values (9, 'FT dumper', '2 Tonne Front Tip Dumper', 200);
insert into plant_inventory_entry (id, name, description, price)
    values (10, 'FT dumper', '2 Tonne Front Tip Dumper', 300);
insert into plant_inventory_entry (id, name, description, price)
    values (11, 'FT dumper', '3 Tonne Front Tip Dumper', 400);
insert into plant_inventory_entry (id, name, description, price)
    values (12, 'Loader', 'Hewden Backhoe Loader', 200);
insert into plant_inventory_entry (id, name, description, price)
    values (13, 'D-Truck', '15 Tonne Articulating Dump Truck', 250);
insert into plant_inventory_entry (id, name, description, price)
    values (14, 'D-Truck', '30 Tonne Articulating Dump Truck', 300);
    
insert into plant_inventory_item (id, condition, plantInfo_id)
	values(1, 'SERVICEABLE', 1);
insert into plant_inventory_item (id, condition, plantInfo_id)
	values(2, 'SERVICEABLE', 2);
insert into plant_inventory_item (id, condition, plantInfo_id)
	values(3, 'SERVICEABLE', 3);
insert into plant_inventory_item (id, condition, plantInfo_id)
	values(4, 'SERVICEABLE', 4);
insert into plant_inventory_item (id, condition, plantInfo_id)
	values(5, 'SERVICEABLE', 5);
insert into plant_inventory_item (id, condition, plantInfo_id)
	values(6, 'SERVICEABLE', 6);
insert into plant_inventory_item (id, condition, plantInfo_id)
	values(7, 'SERVICEABLE', 7);
insert into plant_inventory_item (id, condition, plantInfo_id)
	values(8, 'SERVICEABLE', 8);
insert into plant_inventory_item (id, condition, plantInfo_id)
	values(9, 'SERVICEABLE', 9);
insert into plant_inventory_item (id, condition, plantInfo_id)
	values(10, 'SERVICEABLE', 10);
insert into plant_inventory_item (id, condition, plantInfo_id)
	values(11, 'SERVICEABLE', 11);
insert into plant_inventory_item (id, condition, plantInfo_id)
	values(12, 'SERVICEABLE', 12);
insert into plant_inventory_item (id, condition, plantInfo_id)
	values(13, 'SERVICEABLE', 13);
insert into plant_inventory_item (id, condition, plantInfo_id)
	values(14, 'SERVICEABLE', 14);
	
insert into purchase_order (id, plant_entry_id, start_date, end_date, total)
    values (100, 2, '2016-03-22', '2016-03-24', 600);
    
insert into plant_reservation (id, plant_id, start_date, end_date, rental_id)
	values (1, 1, '2016-03-22', '2016-03-24', 100);
	
insert into maint_plan (id, year_of_action, item_id) values(1, 2016, 2);
insert into maint_plan (id, year_of_action, item_id) values(2, 2015, 3);
insert into maint_plan (id, year_of_action, item_id) values(3, 2014, 4);
insert into maint_plan (id, year_of_action, item_id) values(4, 2013, 5);
insert into maint_plan (id, year_of_action, item_id) values(5, 2012, 6);
insert into maint_plan (id, year_of_action, item_id) values(6, 2011, 7);
	
insert into maint_task (id, maint_plan_id, description, start_date, end_date, price, type_of_work, reservation_id)
	values(1, 1,'corrective 1','2016-01-22','2016-01-24',10,'CORRECTIVE', 2);
insert into maint_task (id, maint_plan_id, description, start_date, end_date, price, type_of_work, reservation_id)
	values(2, 2, 'corrective 2','2015-01-22','2015-01-24',10,'CORRECTIVE', 3);
insert into maint_task (id, maint_plan_id, description, start_date, end_date, price, type_of_work, reservation_id)
	values(3, 3, 'corrective 3','2014-01-22','2014-01-24',10,'CORRECTIVE', 4);
insert into maint_task (id, maint_plan_id, description, start_date, end_date, price, type_of_work, reservation_id)
	values(4, 4, 'corrective 4','2013-01-22','2013-01-24',10,'CORRECTIVE', 5);
insert into maint_task (id, maint_plan_id, description, start_date, end_date, price, type_of_work, reservation_id)
	values(5, 5, 'corrective 5','2012-01-22','2012-01-24',10,'CORRECTIVE', 6);
insert into maint_task (id, maint_plan_id, description, start_date, end_date, price, type_of_work, reservation_id)
	values(6, 6, 'corrective 6','2011-01-22','2011-01-24',10,'CORRECTIVE', 7);
insert into maint_task (id, maint_plan_id, description, start_date, end_date, price, type_of_work, reservation_id)
	values(7, 2, 'corrective 7','2015-07-22','2015-08-24',10,'CORRECTIVE', 8);
