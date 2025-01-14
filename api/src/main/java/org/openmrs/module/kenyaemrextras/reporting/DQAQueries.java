/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemrextras.reporting;

/**
 * A holder of DQA queries: Nov 2021
 */
public class DQAQueries {
	
	/**
	 * TX CURR query TODO: consume the TX CURR query in KenyaEMR for consistency
	 * 
	 * @return
	 */
	public static String getTXCURRQuery() {
		String totalTXCurrQry = "select distinct FLOOR(1 + (RAND() * 999999)) as index_no, t.patient_id\n"
		        + "from(\n"
		        + "    select fup.visit_date,fup.patient_id, max(e.visit_date) as enroll_date,\n"
		        + "           greatest(max(e.visit_date), ifnull(max(date(e.transfer_in_date)),'0000-00-00')) as latest_enrolment_date,\n"
		        + "           greatest(max(fup.visit_date), ifnull(max(d.visit_date),'0000-00-00')) as latest_vis_date,\n"
		        + "           greatest(mid(max(concat(fup.visit_date,fup.next_appointment_date)),11), ifnull(max(d.visit_date),'0000-00-00')) as latest_tca,\n"
		        + "           p.dob as dob,\n"
		        + "           d.patient_id as disc_patient,\n"
		        + "           d.effective_disc_date as effective_disc_date,\n"
		        + "           max(d.visit_date) as date_discontinued,\n"
		        + "           de.patient_id as started_on_drugs\n"
		        + "    from kenyaemr_etl.etl_patient_hiv_followup fup\n"
		        + "           join kenyaemr_etl.etl_patient_demographics p on p.patient_id=fup.patient_id\n"
		        + "           join kenyaemr_etl.etl_hiv_enrollment e on fup.patient_id=e.patient_id\n"
		        + "           left outer join kenyaemr_etl.etl_drug_event de on e.patient_id = de.patient_id and de.program='HIV' and date(date_started) <= date(:endDate)\n"
		        + "           left outer JOIN\n"
		        + "             (select patient_id, coalesce(date(effective_discontinuation_date),visit_date) visit_date,max(date(effective_discontinuation_date)) as effective_disc_date from kenyaemr_etl.etl_patient_program_discontinuation\n"
		        + "              where date(visit_date) <= date(:endDate) and program_name='HIV'\n"
		        + "              group by patient_id\n"
		        + "             ) d on d.patient_id = fup.patient_id\n"
		        + "    where fup.visit_date <= date(:endDate)\n"
		        + "    group by patient_id\n"
		        + "    having (started_on_drugs is not null and started_on_drugs <> '') and (\n"
		        + "        (\n"
		        + "            ((timestampdiff(DAY,date(latest_tca),date(:endDate)) <= 30 or timestampdiff(DAY,date(latest_tca),date(curdate())) <= 30) and ((date(d.effective_disc_date) > date(:endDate) or date(enroll_date) > date(d.effective_disc_date)) or d.effective_disc_date is null))\n"
		        + "              and (date(latest_vis_date) >= date(date_discontinued) or date(latest_tca) >= date(date_discontinued) or disc_patient is null)\n"
		        + "            ))) t;";
		return totalTXCurrQry;
	}
	
	/**
	 * Query for children below 15 years old
	 * 
	 * @return
	 */
	public static String getChildrenBelow15Query() {
		String below15qry = "select distinct FLOOR(1 + (RAND() * 999999)) as index_no, t.patient_id\n"
		        + "from(\n"
		        + "    select fup.visit_date,fup.patient_id, max(e.visit_date) as enroll_date,\n"
		        + "           greatest(max(e.visit_date), ifnull(max(date(e.transfer_in_date)),'0000-00-00')) as latest_enrolment_date,\n"
		        + "           greatest(max(fup.visit_date), ifnull(max(d.visit_date),'0000-00-00')) as latest_vis_date,\n"
		        + "           greatest(mid(max(concat(fup.visit_date,fup.next_appointment_date)),11), ifnull(max(d.visit_date),'0000-00-00')) as latest_tca,\n"
		        + "           p.dob as dob,\n"
		        + "           d.patient_id as disc_patient,\n"
		        + "           d.effective_disc_date as effective_disc_date,\n"
		        + "           max(d.visit_date) as date_discontinued,\n"
		        + "           de.patient_id as started_on_drugs\n"
		        + "    from kenyaemr_etl.etl_patient_hiv_followup fup\n"
		        + "           join kenyaemr_etl.etl_patient_demographics p on p.patient_id=fup.patient_id\n"
		        + "           join kenyaemr_etl.etl_hiv_enrollment e on fup.patient_id=e.patient_id\n"
		        + "           left outer join kenyaemr_etl.etl_drug_event de on e.patient_id = de.patient_id and de.program='HIV' and date(date_started) <= date(:endDate)\n"
		        + "           left outer JOIN\n"
		        + "             (select patient_id, coalesce(date(effective_discontinuation_date),visit_date) visit_date,max(date(effective_discontinuation_date)) as effective_disc_date from kenyaemr_etl.etl_patient_program_discontinuation\n"
		        + "              where date(visit_date) <= date(:endDate) and program_name='HIV'\n"
		        + "              group by patient_id\n"
		        + "             ) d on d.patient_id = fup.patient_id\n"
		        + "    where fup.visit_date <= date(:endDate)\n"
		        + "    group by patient_id\n"
		        + "    having timestampdiff(YEAR ,dob,date(:endDate)) <= 14 and (started_on_drugs is not null and started_on_drugs <> '') and (\n"
		        + "        (\n"
		        + "            ((timestampdiff(DAY,date(latest_tca),date(:endDate)) <= 30 or timestampdiff(DAY,date(latest_tca),date(curdate())) <= 30) and ((date(d.effective_disc_date) > date(:endDate) or date(enroll_date) > date(d.effective_disc_date)) or d.effective_disc_date is null))\n"
		        + "              and (date(latest_vis_date) >= date(date_discontinued) or date(latest_tca) >= date(date_discontinued) or disc_patient is null)\n"
		        + "            ))) t;";
		
		return below15qry;
	}
	
	/**
	 * Query for adults 15+ years
	 * 
	 * @return
	 */
	public static String getAdultAbove15Query() {
		String generalPopQry = "select distinct FLOOR(1 + (RAND() * 999999)) as index_no, t.patient_id\n"
		        + "from(\n"
		        + "    select fup.visit_date,fup.patient_id, max(e.visit_date) as enroll_date,\n"
		        + "           greatest(max(e.visit_date), ifnull(max(date(e.transfer_in_date)),'0000-00-00')) as latest_enrolment_date,\n"
		        + "           greatest(max(fup.visit_date), ifnull(max(d.visit_date),'0000-00-00')) as latest_vis_date,\n"
		        + "           greatest(mid(max(concat(fup.visit_date,fup.next_appointment_date)),11), ifnull(max(d.visit_date),'0000-00-00')) as latest_tca,\n"
		        + "           p.dob as dob,\n"
		        + "           d.patient_id as disc_patient,\n"
		        + "           d.effective_disc_date as effective_disc_date,\n"
		        + "           max(d.visit_date) as date_discontinued,\n"
		        + "           de.patient_id as started_on_drugs\n"
		        + "    from kenyaemr_etl.etl_patient_hiv_followup fup\n"
		        + "           join kenyaemr_etl.etl_patient_demographics p on p.patient_id=fup.patient_id\n"
		        + "           join kenyaemr_etl.etl_hiv_enrollment e on fup.patient_id=e.patient_id\n"
		        + "           left outer join kenyaemr_etl.etl_drug_event de on e.patient_id = de.patient_id and de.program='HIV' and date(date_started) <= date(:endDate)\n"
		        + "           left outer JOIN\n"
		        + "             (select patient_id, coalesce(date(effective_discontinuation_date),visit_date) visit_date,max(date(effective_discontinuation_date)) as effective_disc_date from kenyaemr_etl.etl_patient_program_discontinuation\n"
		        + "              where date(visit_date) <= date(:endDate) and program_name='HIV'\n"
		        + "              group by patient_id\n"
		        + "             ) d on d.patient_id = fup.patient_id\n"
		        + "    where fup.visit_date <= date(:endDate)\n"
		        + "    group by patient_id\n"
		        + "    having timestampdiff(YEAR ,dob,date(:endDate)) >= 15 and (started_on_drugs is not null and started_on_drugs <> '') and (\n"
		        + "        (\n"
		        + "            ((timestampdiff(DAY,date(latest_tca),date(:endDate)) <= 30 or timestampdiff(DAY,date(latest_tca),date(curdate())) <= 30) and ((date(d.effective_disc_date) > date(:endDate) or date(enroll_date) > date(d.effective_disc_date)) or d.effective_disc_date is null))\n"
		        + "              and (date(latest_vis_date) >= date(date_discontinued) or date(latest_tca) >= date(date_discontinued) or disc_patient is null)\n"
		        + "            ))) t;";
		return generalPopQry;
	}
	
	/**
	 * Query for PMTCT
	 * 
	 * @return
	 */
	public static String getPMTCTQuery() {
		String pmtctQry = "select distinct FLOOR(1 + (RAND() * 999999)) as index_no, t.patient_id\n"
		        + "from(\n"
		        + "    select fup.visit_date,fup.patient_id, max(e.visit_date) as enroll_date,\n"
		        + "           greatest(max(e.visit_date), ifnull(max(date(e.transfer_in_date)),'0000-00-00')) as latest_enrolment_date,\n"
		        + "           greatest(max(fup.visit_date), ifnull(max(d.visit_date),'0000-00-00')) as latest_vis_date,\n"
		        + "           greatest(mid(max(concat(fup.visit_date,fup.next_appointment_date)),11), ifnull(max(d.visit_date),'0000-00-00')) as latest_tca,\n"
		        + "           d.patient_id as disc_patient,\n"
		        + "           p.dob as dob,\n"
		        + "           d.effective_disc_date as effective_disc_date,\n"
		        + "           max(d.visit_date) as date_discontinued,\n"
		        + "           de.patient_id as started_on_drugs\n"
		        + "    from kenyaemr_etl.etl_patient_hiv_followup fup\n"
		        + "           join kenyaemr_etl.etl_patient_demographics p on p.patient_id=fup.patient_id\n"
		        + "           join kenyaemr_etl.etl_hiv_enrollment e on fup.patient_id=e.patient_id\n"
		        + "           inner join kenyaemr_etl.etl_patient_program pp on pp.patient_id=fup.patient_id and program='MCH-Mother Services' and (\n"
		        + "    date(date_enrolled) between date(:startDate) and date(:endDate) or \n"
		        + "    date(date_completed) between date(:startDate) and date(:endDate) or\n"
		        + "    (date(date_enrolled) < date(:startDate) and (date_completed is null or date(date_completed) > date(:endDate))) )\n"
		        + "           left outer join kenyaemr_etl.etl_drug_event de on e.patient_id = de.patient_id and de.program='HIV' and date(date_started) <= date(:endDate)\n"
		        + "           left outer JOIN\n"
		        + "             (select patient_id, coalesce(date(effective_discontinuation_date),visit_date) visit_date,max(date(effective_discontinuation_date)) as effective_disc_date from kenyaemr_etl.etl_patient_program_discontinuation\n"
		        + "              where date(visit_date) <= date(:endDate) and program_name='HIV'\n"
		        + "              group by patient_id\n"
		        + "             ) d on d.patient_id = fup.patient_id\n"
		        + "    where fup.visit_date <= date(:endDate)\n"
		        + "    group by patient_id\n"
		        + "    having timestampdiff(YEAR ,dob,date(:endDate)) >= 15 and (started_on_drugs is not null and started_on_drugs <> '') and (\n"
		        + "        (\n"
		        + "            ((timestampdiff(DAY,date(latest_tca),date(:endDate)) <= 30 or timestampdiff(DAY,date(latest_tca),date(curdate())) <= 30) and ((date(d.effective_disc_date) > date(:endDate) or date(enroll_date) > date(d.effective_disc_date)) or d.effective_disc_date is null))\n"
		        + "              and (date(latest_vis_date) >= date(date_discontinued) or date(latest_tca) >= date(date_discontinued) or disc_patient is null)\n"
		        + "            ))) t;";
		return pmtctQry;
	}
}
