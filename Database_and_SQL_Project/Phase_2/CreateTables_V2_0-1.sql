/*
A SQL script that creates the tables.
Evan Christensen
Stephen Marcel
*/

CREATE TABLE person
    (per_id         varchar(5),
     first_name     varchar(15),
     last_name      varchar(25),
     city           varchar(15),
     street_name    varchar(30),
     street_number  numeric(5,0),
     apt            varchar(10),
     state          varchar(2),
     country        varchar(25),
     zip_code       numeric(5,0),
     email          varchar(50),
     PRIMARY KEY (per_id)
    );
    
CREATE TABLE phone    
    (per_id         varchar(5),
     country_code   numeric(3,0),
     area_code      numeric(3,0),
     phone_number   numeric(7,0),
     phone_type     varchar(6),
     PRIMARY KEY (per_id, phone_number),
     FOREIGN KEY (per_id) REFERENCES person  
    
    );

CREATE TABLE course
    (c_code         varchar(10),
     title          varchar(100),
     tier           varchar(15),--get rid of this
     description    varchar(100),
     status         varchar(15),
     retail_price   numeric(6,2),
     prereq         varchar(10),
     institution    varchar(40),
     PRIMARY KEY (c_code),
     FOREIGN KEY (prereq) REFERENCES course (c_code)
        ON DELETE SET NULL
    );

CREATE TABLE section
    (sec_no         varchar(10),
     c_code         varchar(10),
     year           numeric(4,0),
     complete_date  varchar(10),
     offered_by     varchar(40),
     sec_format     varchar(20),
     price          numeric(6,0),
     PRIMARY KEY (sec_no, c_code, year),
     FOREIGN KEY (c_code) REFERENCES course
        ON DELETE CASCADE

    );


CREATE TABLE knowledge_skill
    (ks_code        varchar(10),
     skill_title    varchar(100),
     description    varchar(100),
     tier           varchar(15),
     cc_code        varchar(10),
     PRIMARY KEY (ks_code)
    --FOREIGN KEY (cc_code) REFERENCE teaches
    );

CREATE TABLE skill_category
    (cc_code        varchar(10),
     func_code      varchar(2),
     work_func      varchar(100),
     parent_cc_code varchar(4),
     parent_cc_func varchar(2),
     PRIMARY KEY (cc_code, func_code),
     FOREIGN KEY (parent_cc_code, parent_cc_func) REFERENCES skill_category (cc_code, func_code)
     
    );

CREATE TABLE company
    (comp_id        varchar(10),
     comp_name      varchar(70),
     city           varchar(50),
     street_name    varchar(30),
     street_number  numeric(5,0),
     apt            varchar(10),
     st_abv         varchar(2),
     country        varchar(15),
     zip_code       numeric(5,0),
     industry_group varchar(40),
     website        varchar(40),
     PRIMARY KEY (comp_id)

    );

CREATE TABLE GCIS
    (industry_code   varchar(10),
     ind_title       varchar(50),
     industry_desc   varchar(70),
     parent_code     varchar(10),
     tier            varchar(15),
     PRIMARY KEY (industry_code),
     FOREIGN KEY (parent_code) REFERENCES GCIS (industry_code)
    );
   
CREATE TABLE sub_ind
    (comp_id        varchar(10),
     industry_code  varchar(10),
     PRIMARY KEY (comp_id),
     FOREIGN KEY (comp_id) REFERENCES company,
     FOREIGN KEY (industry_code) REFERENCES GCIS
    
    );

CREATE TABLE job_category
    (job_cate       varchar(10),
     title          varchar(50),
     description    varchar(50),
     pay_range_high numeric(10,2),
     pay_range_low  numeric(15,2),
     parent_cate    varchar(10),
     PRIMARY KEY (job_cate),
     FOREIGN KEY (parent_cate) REFERENCES job_category (job_cate)
    
    );

CREATE TABLE position
    (pos_code       varchar(10),
     pos_name       varchar(100),
     emp_mode       varchar(15),--remove this
     pay_rate       numeric(10,2),
     pay_type       varchar(10),
     job_cate       varchar(10),
     comp_id        varchar(10),
     PRIMARY KEY (pos_code),
     FOREIGN KEY (job_cate) REFERENCES job_category (job_cate),
     FOREIGN KEY (comp_id) REFERENCES company

    );


CREATE TABLE certificate
    (cert_code      varchar(10),
     t_code         varchar(10),
     title          varchar(50),
     expire_date    varchar(10),
     issued_by      varchar(25),
     PRIMARY KEY (cert_code)
     
    );

CREATE TABLE works
    (per_id         varchar(5),
     pos_code       varchar(10),
     hire_date      varchar(10),
     leave_date     varchar(10),
     PRIMARY KEY (per_id, pos_code),
     FOREIGN KEY (per_id) REFERENCES person,
     FOREIGN KEY (pos_code) REFERENCES position
    );
    
CREATE TABLE has_skill
    (per_id         varchar(5),
     ks_code        varchar(10),
     PRIMARY KEY (per_id, ks_code),
     FOREIGN KEY (per_id) REFERENCES person,
     FOREIGN KEY (ks_code) REFERENCES knowledge_skill
    
    );

CREATE TABLE pos_requires
    (ks_code        varchar(10),
     pos_code       varchar(10),
     tier           varchar(15), --remove this
     PRIMARY KEY (ks_code,pos_code),
     FOREIGN KEY (ks_code) REFERENCES knowledge_skill,
     FOREIGN KEY (pos_code) REFERENCES position
    
    );

CREATE TABLE core_skill
    (cc_code        varchar(10),
     job_cate       varchar(50),
     PRIMARY KEY (cc_code, job_cate),
     FOREIGN KEY (job_cate) REFERENCES job_category
    );

CREATE TABLE issues
    (c_code         varchar(10),
     cert_code      varchar(10),
     PRIMARY KEY (c_code),
     FOREIGN KEY (c_code) REFERENCES course,
     FOREIGN KEY (cert_code) REFERENCES certificate
    
    );

CREATE TABLE teaches
    (c_code         varchar(10),
     ks_code        varchar(10),
     PRIMARY KEY (c_code, ks_code),
     FOREIGN KEY (c_code) REFERENCES course,
     FOREIGN KEY (ks_code) REFERENCES knowledge_skill
    
    );

CREATE TABLE takes
    (per_id         varchar(5),
     sec_no         varchar(10),
     c_code         varchar(10),
     year           numeric(4,0),
     PRIMARY KEY (per_id, sec_no, c_code, year),
     FOREIGN KEY (per_id) REFERENCES person,
     FOREIGN KEY (c_code) REFERENCES course,
     FOREIGN KEY (sec_no, c_code, year) REFERENCES section
    
    );

CREATE TABLE requires_cert
    (pos_code       varchar(10),
     cert_code      varchar(10),
     PRIMARY KEY (pos_code, cert_code),
     FOREIGN KEY (pos_code) REFERENCES position,
     FOREIGN KEY (cert_code) REFERENCES certificate
    
    );
    
CREATE TABLE skill_type
    (cc_code        varchar(10),
     ks_code        varchar(10),
     PRIMARY KEY (ks_code),
     FOREIGN KEY (ks_code) REFERENCES knowledge_skill
    
    );

CREATE TABLE has_cert
    (per_id         varchar(10),
     cert_code      varchar(10),
     PRIMARY KEY (per_id, cert_code),
     FOREIGN KEY (per_id) REFERENCES person,
     FOREIGN KEY (cert_code) REFERENCES certificate
);