create table traffic_event (
	id varchar(255) not null,
	vehicle_id varchar(255) not null,
	vehicle_brand smallint not null check (vehicle_brand between 0 and 9),
	timestamp bigint not null check (timestamp >= 0),
	primary key (id));

create index IDX_Traffic_Event_Timestamp on traffic_event (
	timestamp)