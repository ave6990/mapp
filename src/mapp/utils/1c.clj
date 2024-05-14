;; Update 'refs' table with 'temp' table data.
(map (fn [r]
         (jdbc/update!
            midb
            :refs
            {
              :expiration_date (:expiration_date r)
              :service_date (:service_date r)
              :manufacture_year (:manufacture_year r)
              :rosakr (:rosakr r)
              :rosakr_expiration (:rosakr_expiration r)
              :date (:date r)
              :update_date (:update_date r)
              :publication_date (:publication_date r)
              :code_fif (:code_fif r)
              :engineer (:engineer r)}
            ["number_1c = ?" (:number_1c r)]))
     (jdbc/query
       midb
       "select
          refs.number_1c,
          temp.expiration_date,
          temp.service_date,
          temp.manufacture_year,
          temp.rosakr,
          temp.rosakr_expiration,
          temp.date,
          temp.update_date,
          temp.publication_date,
          temp.code_fif,
          temp.engineer
        from
          refs
        left join
          temp on temp.number_1c = refs.number_1c
        where refs.number_1c <> '-'"))
