ALTER TABLE company ADD COLUMN registration_number VARCHAR(255);
ALTER TABLE company ADD COLUMN entity_type VARCHAR(255);
ALTER TABLE company ADD COLUMN industry_sector VARCHAR(255);
ALTER TABLE company ADD COLUMN date_of_incorporation DATE;
ALTER TABLE company ADD COLUMN registered_address VARCHAR(255);
ALTER TABLE company ADD COLUMN operating_address VARCHAR(255);
ALTER TABLE company ADD COLUMN country VARCHAR(255);
ALTER TABLE company ADD COLUMN company_size VARCHAR(255);
ALTER TABLE company ADD COLUMN main_contact_name VARCHAR(255);
ALTER TABLE company ADD COLUMN main_contact_email VARCHAR(255);
ALTER TABLE company ADD COLUMN main_contact_phone VARCHAR(255);
ALTER TABLE company ADD COLUMN contact_person_role VARCHAR(255);
ALTER TABLE company ADD COLUMN secondary_contact_name VARCHAR(255);
ALTER TABLE company ADD COLUMN technical_contact_email VARCHAR(255);
ALTER TABLE company ADD COLUMN billing_contact_email VARCHAR(255);
ALTER TABLE company ADD COLUMN authorized_persons VARCHAR(255);
ALTER TABLE company ADD COLUMN emergency_contact_number VARCHAR(255);
ALTER TABLE company ADD COLUMN preferred_language VARCHAR(255);
ALTER TABLE company ADD COLUMN tax_id_number VARCHAR(255);
ALTER TABLE company ADD COLUMN bank_name VARCHAR(255);
ALTER TABLE company ADD COLUMN bank_account_number VARCHAR(255);
ALTER TABLE company ADD COLUMN preferred_payment_method VARCHAR(255);
ALTER TABLE company ADD COLUMN role_on_platform VARCHAR(255);
ALTER TABLE company ADD COLUMN requested_features VARCHAR(255);
ALTER TABLE company ADD COLUMN operating_hours VARCHAR(255);
ALTER TABLE company ADD COLUMN has_compliance_certification BOOLEAN;
ALTER TABLE company ADD COLUMN agreed_to_terms_of_service BOOLEAN;
ALTER TABLE company ADD COLUMN agreed_onboarding_date DATE;
