(ns mapp.db.queries
  (:require
    [clojure.string :as string]))

;;#get
(def get-verifications-records-count
  "select count(*) as count
  from
  (select * from 
  (select
    v.id as id, v.engineer, v.count, v.counteragent, v.conditions,
    v.verification_type,
    v.protocol_number, v.mi_type, v.methodology_id, v.serial_number,
    v.manufacture_year, v.channels, v.area, v.period, v.components,
    v.scope, v.sw_name, v.sw_version, v.sw_version_real,
    v.sw_checksum, v.sw_algorithm, v.protocol,
    v.protolang, v.voltage as v_voltage, v.other_conditions as v_other_conditions
    , v.upload, v.comment, v.copy_from,
    v.hash_refs, c.date, c.temperature, c.humidity, c.pressure, c.voltage, c.frequency,
    c.other, c.location, c.comment as c_comment, ca.id as ca_id,
    ca.name as ca_name, ca.short_name as ca_short_name, ca.address, ca.inn,
    ca.type as ca_type, met.registry_number as registry_number, met.name as met_name,
    met.short_name as met_short_name, met.approved, met.date_from as met_date_from,
    met.date_to as met_date_to, met.temperature as met_temperature,
    met.humidity as met_humidity, met.pressure as met_pressure,
    met.voltage as met_voltage, met.frequency as met_frequency,
    met.other as met_other, met.limited
  from
      verification as v
  left join
      conditions as c
      on c.id = v.conditions
  inner join
      methodology as met
      on met.id = v.methodology_id
  inner join
      counteragents as ca
      on ca.id = v.counteragent)
  {where}
  {group-by});")

(def get-verifications
  "select *
  from
  (select
    v.id as id, v.engineer, v.count, v.counteragent, v.conditions,
    v.verification_type,
    v.protocol_number, v.mi_type, v.methodology_id, v.serial_number,
    v.manufacture_year, v.channels, v.area, v.period, v.components,
    v.scope, v.sw_name, v.sw_version, v.sw_version_real,
    v.sw_checksum, v.sw_algorithm, v.protocol,
    v.protolang, v.voltage as voltage, v.other_conditions as v_other_conditions
    , v.upload, v.comment, v.copy_from,
    v.hash_refs, c.date, c.temperature, c.humidity, c.pressure, c.voltage as c_voltage, c.frequency,
    c.other, c.location, c.comment as c_comment, ca.id as ca_id,
    ca.name as ca_name, ca.short_name as ca_short_name, ca.address, ca.inn,
    ca.type as ca_type, met.registry_number as registry_number, met.name as met_name,
    met.short_name as met_short_name, met.approved, met.date_from as met_date_from,
    met.date_to as met_date_to, met.temperature as met_temperature,
    met.humidity as met_humidity, met.pressure as met_pressure,
    met.voltage as met_voltage, met.frequency as met_frequency,
    met.other as met_other, met.limited
  from
      verification as v
  left join
      conditions as c
      on c.id = v.conditions
  inner join
      methodology as met
      on met.id = v.methodology_id
  inner join
      counteragents as ca
      on ca.id = v.counteragent)
  {where}
  {group-by}
  order by
    upload,
    date desc,
    id desc
  {limit}
  {offset};")

(def get-conditions-records-count
  "select
    count(*) as count
  from
  (select *
  from
    conditions
  {where});")

(def get-conditions
  "select
    *
  from
    conditions
  {where}
  order by
    date desc
  {limit}
  {offset};")

(def get-journal-records-count
  "select
    count(*) as count
  from
  (select *
   from
    journal
  {where});")

(def get-journal
  "select *
   from
     journal
   {where}
   order by
     cast(protocol_number as integer)
   {limit}
   {offset};")

(def get-gso-records-count
  "select
    count(*) as count
  from
  (select *
  from
    gso
  {where});")

(def get-gso
  "select
    *
  from
    gso
  {where}
  order by
    available desc, components, conc
  {limit}
  {offset};")

(def get-counteragents-records-count
  "select
    count(*) as count
  from
  (select *
  from
    counteragents
  {where});")

(def get-counteragents
  "select
    *
  from
    counteragents
  {where}
  {limit}
  {offset};")

(def get-references-records-count
  "select
    count(*) as count
  from
  (select *
  from
    view_refs_use_count
  {where});")

(def get-references
  "select
    *
  from
    view_refs_use_count
  {where}
  {limit}
  {offset};")

(def get-channels-records-count
  "select
    count(*) as count
  from
  (select *
  from
    channels
  {where});")

(def get-channels
  "select *
  from
    channels
  {where}
  {limit}
  {offset};")

(def get-metrology-records-count
  "select
    count(*) as count
  from
  (select *
  from
    metrology as metr
  inner join
    channels as ch
    on ch.id = metr.channel_id
  inner join
    methodology as met
    on met.id = ch.methodology_id
  {where});")

(def get-metrology
  "select 
    metr.id
    , met.registry_number
    , ch.id as channel_id
    , ch.component || ' (' || ch.range_from || ' - ' || ch.range_to
      || ') ' || ch.units as channel
    , metr.r_from
    , metr.r_to
    , metr.value
    , metr.fraction
    , metr.type_id
    , metr.name
    , metr.units
    , metr.operation_id
    , metr.text
    , metr.comment
  from
    metrology as metr
  inner join
    channels as ch
    on ch.id = metr.channel_id
  inner join
    methodology as met
    on met.id = ch.methodology_id
  {where}
  order by ch.id
  {limit}
  {offset};")

(def get-verification-operations-records-count
  "select
    count(*) as count
  from
  (select *
  from
    verification_operations as op
  inner join
    methodology as met
    on met.id = op.methodology_id
  {where});")

(def get-verification-operations
  "select
    op.id,
    op.methodology_id,
    met.registry_number,
    met.short_name,
    op.section,
    op.name,
    op.verification_type,
    op.comment,
    op.info,
    met.mi_types
  from
    verification_operations as op
  inner join
    methodology as met
    on met.id = op.methodology_id
  {where}
  order by met.id
  {limit}
  {offset};")

(def get-v-operations-records-count
  "select
    count(*) as count
  from
  (select *
  from
    v_operations as v_op
  inner join
    verification_operations as op
    on op.id = v_op.op_id
  inner join
    methodology as met
    on met.id = op.methodology_id
  {where});")

(def get-v-operations
  "select
    v_op.id,
    v_op.op_id,
    v_op.v_id,
    v_op.result,
    v_op.unusability,
    met.registry_number,
    op.section,
    op.name,
    op.verification_type,
    op.comment
  from
    v_operations as v_op
  inner join
    verification_operations as op
    on op.id = v_op.op_id
  inner join
    methodology as met
    on met.id = op.methodology_id
  {where}
  {limit}
  {offset};")

(def get-measurements-records-count
  "select
    count(*) as count
  from
  (select
    *
  from
      measurements as meas
  inner join
      metrology as metr
      on metr.id = meas.metrology_id
  inner join
      channels as ch
      on ch.id = metr.channel_id    
  inner join
      characteristics as chr
      on chr.id = metr.type_id
  left join
      ref_values as rv
      on rv.id = meas.ref_value_id
  inner join    
      verification as v
      on meas.v_id = v.id
  {where});")

(def get-measurements
  "select
      meas.id,
      meas.v_id,
      v.serial_number,
      v_ops.section || ' ' || v_ops.name as operation,
      meas.metrology_id,
      meas.channel_name,
      ifnull(ifnull(ch.channel, ch.component) || ' ', '')
        || ifnull('(' || metr.r_from || ' - ' || metr.r_to || ') ' || ch.units || '; ', '')
        || ifnull(chr.symbol || ' ', '')
        || ifnull(chr.comparison || ' ', '')
        || coalesce(ifnull(metr.value, 0) + ifnull(metr.fraction, 0) * meas.ref_value || ' ', metr.value, '')
        || coalesce(iif(metr.type_id > 0 and metr.type_id < 3, '%', metr.units), ch.units, '') as channel,
            meas.value,
      meas.value_2,
      meas.ref_value,
      meas.ref_value_id,
      rv.nominal_range,
      meas.text,
      metr.value as error_value,
      metr.fraction as error_fraction,
      metr.units as error_units,
      metr.type_id as error_type,
      meas.unusability,
      meas.operation_id,
      meas.comment
  from
      measurements as meas
  left join
      metrology as metr
      on metr.id = meas.metrology_id
  left join
      channels as ch
      on ch.id = metr.channel_id    
  left join
      characteristics as chr
      on chr.id = metr.type_id
  inner join    
      verification as v
      on meas.v_id = v.id
  left join
      verification_operations as v_ops
      on v_ops.id = meas.operation_id
  left join
    ref_values as rv
    on rv.id = meas.ref_value_id
  {where}
  order by
    meas.v_id, meas.operation_id
  {limit}
  {offset};")

(def get-ref-values-records-count
  "select count(*) as count
  from
  (select *
  from
    ref_values as rv
  inner join
    metrology as metr
    on metr.id = rv.metrology_id
  inner join
    channels as ch
    on ch.id = metr.channel_id
  inner join
    methodology as met
    on met.id = ch.methodology_id
  {where});")

(def get-ref-values
  "select 
    rv.id
    , met.registry_number
    , ch.channel as channel_name
    , ifnull(ch.component || ' ', '') || '(' || ch.range_from || ' - '
      || ch.range_to || ') ' || ch.units as channel
    , '(' || metr.r_from || ' - ' || metr.r_to || ') '
      || ifnull(metr.units, ch.units) as range
    , rv.metrology_id
    , rv.number
    , rv.nominal_range
    , rv.nominal
    , rv.units
    , rv.tolerance
    , rv.tolerance_type
    , rv.reference
    , rv.level
    , rv.error
    , rv.error_type
    , rv.comment
  from
    ref_values as rv
  inner join
    metrology as metr
    on metr.id = rv.metrology_id
  inner join
    channels as ch
    on ch.id = metr.channel_id
  inner join
    methodology as met
    on met.id = ch.methodology_id
  {where}
  order by ch.id
  {limit}
  {offset};")

(def get-refs-set-records-count
  "select count(*) as count
  from
    verification_refs
  {where};")

(def get-refs-set
  "select *
  from
    verification_refs
  {where}
  order by
    v_id desc
  {limit}
  {offset};")

(def get-methodology-records-count
  "select count(*) as count
  from
    methodology
  {where};")

(def get-methodology
  "select *
  from
    methodology
  {where}
  order by
    id desc
  {limit}
  {offset};")

;;#copy
(def last-id
  "select id from verification order by id desc limit 1;")

(def next-id
  "select id from verification
   where protocol_number is null limit 1;")

(def next-protocol-number
  "select protocol_number + 1 as protocol_number
   from verification
   where protocol_number is not null
   order by id desc
   limit 1;")

(def copy-verification
  "Query to copy the record with id from the verification table"
  "with temp as (
      select
        ? as v_id
      )
  insert into
      verification (
          engineer, count, counteragent, conditions, mi_type, methodology_id,
          serial_number, manufacture_year, components,
          scope, channels, area, period, verification_type,
          sw_name, sw_version, sw_version_real, sw_checksum,
          sw_algorithm, voltage, protocol, protolang, copy_from, comment
      )
  select
      engineer, count, counteragent, conditions, mi_type, methodology_id,
      serial_number, manufacture_year, components, scope,
      channels, area, period, verification_type, sw_name,
      sw_version, sw_version_real, sw_checksum, sw_algorithm,
      voltage, protocol, protolang,
      (select v_id from temp), comment
  from
      verification
  where
      id = (select v_id from temp);")

(def copy-v-gso
  "Query to copy gso."
  "insert into v_gso
     (v_id, gso_id)
  select
      ?,
      gso_id
  from
      v_gso
  where
      v_id = ?;")

(def copy-v-refs
  "Query to copy refs."
  "insert into v_refs
    (v_id, ref_id)
  select
      ?,
      ref_id
  from
      v_refs
  where
      v_id = ?;")

(def copy-v-opt-refs
  "Query to copy refs."
  "insert into v_opt_refs
    (v_id, ref_id)
  select
      ?,
      ref_id
  from
      v_opt_refs
  where
      v_id = ?;")

;; TODO: delete
(def copy-v-operations
  "Query to copy operations."
  "insert into v_operations
    (v_id, op_id, result)
  select
      ?,
      op_id,
      1
  from
      v_operations
  where
      v_id = ?;")

(def copy-measurements
  "Query to copy measurements."
  "insert into measurements
      (v_id, metrology_id, channel_name, ref_value, operation_id, ref_value_id, comment)
  select
      ?,
      metrology_id,
      channel_name,
      ref_value,
      operation_id,
      ref_value_id,
      comment
  from
      measurements
  where
      v_id = ?;")
