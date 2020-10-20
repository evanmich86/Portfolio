--Stephen Marcel
--Evan Christensen

--1. List a specific company’s workers by names. 
WITH current_emps AS(
    SELECT per_id, pos_code
    FROM works
    WHERE works.leave_date IS null) --returns a table with per_ids that are currently working a position
SELECT first_name, last_name
FROM person NATURAL JOIN (position NATURAL JOIN current_emps) --links names with the per_ids of currently employed people
WHERE position.comp_id = '409382';


--2. List a specific company’s staff (salary workers) 
--   by salary in descending order.
WITH current_emps AS(
    SELECT per_id, pos_code
    FROM works
    WHERE works.leave_date IS null)
SELECT per_id, first_name, last_name, pay_rate
FROM position NATURAL JOIN (person NATURAL JOIN current_emps)
WHERE comp_id = '687331' AND
        pay_type = 'salary'
ORDER BY position.pay_rate DESC;


--3. List the average annual pay (the salary or wage rates multiplying 
--   by 1920 hours) of each company in descending order.
WITH current_emps
  AS (SELECT per_id, pos_code
        FROM works
       WHERE leave_date IS NULL), --returns a table of currently employed people
job_rel_pay
  AS (SELECT pos_code, comp_id,
             CASE pay_type
             WHEN 'hourly'
             THEN pay_rate * 1920
             WHEN 'salary'
             THEN pay_rate --* 1920 / 2080
              END AS pay
        FROM position)--returns the pay either salary or hourly(for a whole year)
SELECT comp_name, ROUND(pay_avg, 2) AS annual_pay--rounds the averages to 2 decimal places
  FROM (SELECT comp_name, AVG(pay) AS pay_avg
          FROM person
               INNER JOIN current_emps
               ON person.per_id = current_emps.per_id
               INNER JOIN job_rel_pay
               ON current_emps.pos_code = job_rel_pay.pos_code
               INNER JOIN company
               ON job_rel_pay.comp_id = company.comp_id
         GROUP BY comp_name)
 ORDER BY pay_avg DESC;


--4. List the average, maximum and minimum annual pay (total salaries 
--   or wage rates multiplying by 1920 hours) of  each industry (listed 
--   in GICS) in the order of the industry names.
--industry salaries
WITH ind_sal AS(
    SELECT gcis.ind_title, salaries
    FROM GCIS NATURAL JOIN sub_ind NATURAL JOIN company NATURAL JOIN
        (SELECT comp_id, pay_rate*1920 AS salaries 
             FROM position 
             WHERE pay_type = 'hourly'
             UNION
             SELECT comp_id, pay_rate AS salaries
             FROM position 
             WHERE pay_type = 'salary'))             
SELECT ind_sal.ind_title, AVG(salaries) avg_sal, MAX(salaries) max_sal, MIN(salaries) min_sal                  
FROM ind_sal INNER JOIN GCIS
ON ind_sal.ind_title = GCIS.ind_title
GROUP BY ind_sal.ind_title;


--5. Find out the biggest employer, industry, and industry group 
--   in terms of number of employees. (Three queries) 
--Biggest employer: referenced ex 3.10 hwk problem
WITH current_emps AS(
    SELECT per_id, pos_code
    FROM works
    WHERE works.leave_date IS null)
SELECT comp_name
FROM (SELECT comp_name
          FROM company
               INNER JOIN position
               ON company.comp_id = position.comp_id
               INNER JOIN current_emps
               ON position.pos_code = current_emps.pos_code
         GROUP BY comp_name
         ORDER BY COUNT(per_id) DESC)
 WHERE ROWNUM = 1;


--biggest industry
SELECT *
  FROM (SELECT industry_group
          FROM company
               INNER JOIN position
               ON company.comp_id = position.comp_id
               INNER JOIN works
               ON position.pos_code = works.pos_code
                   AND leave_date IS NULL
         GROUP BY industry_group
         ORDER BY COUNT(per_id))
 WHERE ROWNUM = 1;

 
--biggest industry Group
WITH current_emps AS(
    SELECT per_id, pos_code
    FROM works
    WHERE works.leave_date IS null),
temp AS (
        SELECT parent_code, COUNT(DISTINCT per_id) AS employees
        FROM GCIS NATURAL JOIN sub_ind NATURAL JOIN company NATURAL JOIN position NATURAL JOIN current_emps
        GROUP BY parent_code)
SELECT ind_title, employees
FROM temp INNER JOIN GCIS
        ON temp.parent_code = industry_code
        ORDER BY employees DESC


--6. Find out the job distribution among industries by showing the 
--   number of employees in each industry.
WITH current_emps AS(
    SELECT per_id, pos_code
    FROM works
    WHERE works.leave_date IS null),
temp AS (
        SELECT parent_code, COUNT(DISTINCT per_id) AS employees
        FROM GCIS NATURAL JOIN sub_ind NATURAL JOIN company NATURAL JOIN position NATURAL JOIN current_emps
        GROUP BY parent_code)
SELECT ind_title, employees
FROM temp CROSS JOIN GCIS
WHERE temp.parent_code = industry_code


--7. Given a person’s identifier, find all the job positions this
--   person is currently holding and worked in the past.
SELECT pos_code, pos_name
FROM works NATURAL JOIN position
WHERE per_id = '1000C';


--8.Given a person’s identifier, list this
--person’s knowledge/skills in a readable format.
SELECT per_id, skill_title, ks_code
FROM has_skill NATURAL JOIN knowledge_skill
WHERE per_id = '1000B';


--9.Given a person’s identifier, show the distribution of his/her skills by 
--listing the number of skills in each of the cc_code in Table A.
SELECT cc_code, COUNT(ks_code) dist
FROM has_skill NATURAL JOIN knowledge_skill
WHERE per_id = '1000B'
GROUP BY cc_code;


--10.List the required knowledge/skills of a given pos_code in a readable format.
SELECT skill_title, ks_code
FROM pos_requires NATURAL JOIN knowledge_skill
WHERE pos_code = '00125';


--11.List the required skill categories of a given job category code in a 
--readable format. 
SELECT job_cate, cc_code
FROM (job_category NATURAL JOIN core_skill)


--12.Given a person’s identifier, list a person’s missing knowledge/skills for 
--a specific pos_code in a readable format.
SELECT ks_code
FROM (SELECT ks_code
      FROM pos_requires
      WHERE pos_code = '06238'
      MINUS
      SELECT ks_code
      FROM has_skill
      WHERE per_id = '1000D');


--12-c. Includes certs
WITH missing_skills AS(
    SELECT ks_code
    FROM (SELECT ks_code
          FROM pos_requires
          WHERE pos_code = '06238'
          MINUS
          SELECT ks_code
          FROM has_skill
          WHERE per_id = '1000D')
),
missing_certs AS(
    SELECT cert_code
    FROM (SELECT cert_code
          FROM requires_cert
          WHERE pos_code = '06238'
          MINUS
          SELECT cert_code
          FROM has_cert
          WHERE per_id = '1000D') 
)
SELECT ks_code, cert_code
FROM missing_skills LEFT OUTER JOIN missing_certs
    ON cert_code IS NOT NULL;



--13.Given a person’s identifier and a pos_code, list the courses
--(course id and title) that each alone teaches all the missing knowledge/skills 
--for this person to be qualified for the specified job position. 
WITH skills_required AS (
    SELECT ks_code
    FROM pos_requires
    WHERE pos_code = '06238'
    MINUS
    SELECT ks_code
    FROM has_skill 
    WHERE per_id = '10010')
SELECT teaches.c_code, course.title
FROM teaches INNER JOIN course
ON teaches.c_code = course.c_code NATURAL JOIN skills_required;


--13-c Includes certification
SELECT course.c_code, course.title AS course_title, cert_code
    FROM issues INNER JOIN course
    ON issues.c_code = course.c_code 
        NATURAL JOIN (
            SELECT cert_code
            FROM requires_cert
            WHERE pos_code = '06238'
            MINUS
            SELECT cert_code
            FROM has_cert 
            WHERE per_id = '1000D')
    UNION
SELECT teaches.c_code, course.title, NULL 
FROM teaches INNER JOIN course
    ON teaches.c_code = course.c_code 
    NATURAL JOIN (
        SELECT ks_code
        FROM pos_requires
        WHERE pos_code = '06238'
        MINUS
        SELECT ks_code
        FROM has_skill 
        WHERE per_id = '10010');

--14.Suppose the skill gap of a worker and the requirement of a desired job 
--position can be covered by one course. Find the cheapest course to make up one’s 
--skill gap by showing the course with the lowest minimum section price.
WITH required_courses AS (
    SELECT DISTINCT T.c_code
    FROM teaches T
    WHERE NOT EXISTS(
        (SELECT pos_requires.ks_code
        FROM pos_requires
        WHERE pos_code = '00125')
        MINUS
        (SELECT has_skill.ks_code
         FROM has_skill
         WHERE per_id = '1000A')
        MINUS
        (SELECT ks_code
         FROM teaches S
         WHERE S.c_code = T.c_code)))
SELECT c_code, title, retail_price
FROM section NATURAL JOIN required_courses NATURAL JOIN course
WHERE retail_price = (SELECT MIN(retail_price)
                      FROM required_courses); 


--15.Given a person’s identifier, find the job position with the highest pay rate 
--for this person according to his/her skill possession. 
WITH qualified_jobs AS( 
    --use division to find all jobs the person is qualified for
    SELECT *
    FROM position p 
    WHERE NOT EXISTS(
        (SELECT ks_code
        FROM pos_requires r 
        WHERE p.pos_code = r.pos_code)
        MINUS 
        (SELECT ks_code  
        FROM has_skill
        WHERE per_id = '10010'))
),
annual_salaries AS( 
    --convert hourly pay to annual pay
    SELECT pos_name, 
        CASE pay_type
        WHEN 'hourly'
        THEN pay_rate * 1920
        WHEN 'salary'
        THEN pay_rate
        END AS annual_pay
    FROM qualified_jobs)
--select the highest paying job 
SELECT A.pos_name, A.annual_pay
FROM annual_salaries A CROSS JOIN (
    SELECT MAX(annual_pay) AS annual_pay
    FROM annual_salaries) B 
WHERE A.annual_pay = B.annual_pay;


--15-c includes certs
WITH qualified_jobs AS( 
 --use division to find all jobs the person is qualified for
SELECT * --skills
FROM (SELECT pos_code
      FROM position p 
      WHERE NOT EXISTS(
          (SELECT ks_code
          FROM pos_requires r 
          WHERE p.pos_code = r.pos_code)
          MINUS 
          (SELECT ks_code  
          FROM has_skill
          WHERE per_id = '10010')))
    NATURAL JOIN ( --certs
        SELECT pos_code
        FROM position p 
        WHERE NOT EXISTS(
            (SELECT cert_code
            FROM requires_cert r 
            WHERE p.pos_code = r.pos_code)
            MINUS 
            (SELECT cert_code  
            FROM has_cert
            WHERE per_id = '10010')))
),
annual_salaries AS( 
    --convert hourly pay to annual pay
    SELECT pos_name, 
        CASE pay_type
        WHEN 'hourly'
        THEN pay_rate * 1920
        WHEN 'salary'
        THEN pay_rate
        END AS annual_pay
    FROM qualified_jobs NATURAL JOIN position)
--select the highest paying job 
SELECT A.pos_name, A.annual_pay
FROM annual_salaries A CROSS JOIN (
    SELECT MAX(annual_pay) AS annual_pay
    FROM annual_salaries) B 
WHERE A.annual_pay = B.annual_pay;


--16.Given a position code, list all the names along with the emails of the 
--persons who are qualified for this position. 
SELECT first_name, last_name, email
FROM person P
WHERE NOT EXISTS( 
    --all skills required by position '00126'
    (SELECT ks_code
    FROM pos_requires 
    WHERE pos_code = '00126')
    MINUS 
    --each person that has skills
    (SELECT ks_code 
    FROM has_skill H
    WHERE P.per_id = H.per_id))


--16 -c Includes certs
SELECT first_name, last_name, email
FROM person NATURAL JOIN (
        (SELECT per_id--person has skills for position
         FROM person P 
         WHERE NOT EXISTS( 
            (SELECT ks_code
            FROM pos_requires 
            WHERE pos_code = '00126')
            MINUS 
            (SELECT ks_code 
            FROM has_skill H
            WHERE P.per_id = H.per_id)))
    NATURAL JOIN
        (SELECT per_id -- person has certs for position
         FROM person P
         WHERE NOT EXISTS( 
            (SELECT cert_code
            FROM requires_cert 
            WHERE pos_code = '00126')
            MINUS 
            --each person that has skills
            (SELECT cert_code 
            FROM has_cert H
            WHERE P.per_id = H.per_id)))
);


--17. When a company cannot find any qualified person for a job position, 
--    a secondary solution is to find a person who is almost qualified to the 
--    job position. Make a “missing-k” list that lists people who miss only k
--    skills for a specified pos_code; k< 4. 

--group all people who have skills in the position
WITH have_k AS(
SELECT per_id, COUNT(per_id) have
FROM pos_requires NATURAL JOIN has_skill
WHERE pos_code = '00125' 
GROUP BY per_id),
--make table of skill deficits for position
missing_k AS(
    SELECT per_id, (need - have) AS deficit
    FROM have_k NATURAL JOIN (
        SELECT COUNT(ks_code) AS NEED
        FROM pos_requires
        WHERE pos_code = '00125'
    )
)
--select the persons where their deficit = k 
SELECT per_id, deficit AS missing_sk
FROM missing_k
WHERE deficit = 1 


--18. Suppose there is a new position that has nobody qualified.  List 
--    the persons who miss the least number of skills that are required 
--    by this pos_code and report the “least number”.

--group all people who have skills in the position
WITH have_k AS(
SELECT per_id, COUNT(per_id) have
FROM pos_requires NATURAL JOIN has_skill
WHERE pos_code = '00125' 
GROUP BY per_id),
--make table of skill deficits for position
missing_k AS(
    SELECT per_id, (need - have) AS deficit
    FROM have_k NATURAL JOIN (
        SELECT COUNT(ks_code) AS NEED
        FROM pos_requires
        WHERE pos_code = '00125'
    )
)
--select the persons where their deficit = the smallest deficit 
SELECT per_id, least_numb
FROM missing_k, (
    SELECT MIN(deficit) AS least_numb 
    FROM missing_k) defic
WHERE deficit = least_numb


--19. List each of the skill code and the number of people who are missing the skills 
--    and are in the missing-k list for a given position code in the ascending order 
--    of the people counts. 
--group all people who have skills in the position
WITH have_k AS(
    SELECT per_id, ks_code have
    FROM pos_requires NATURAL JOIN has_skill
    WHERE pos_code = '00125' 
),
missing_k AS(
--returns people who are missing skills and the skills they are missing
    --table of skills have plus skills need
    (SELECT per_id, need
    FROM have_k, (
        SELECT ks_code AS need
        FROM pos_requires
        WHERE pos_code = '00125'
    ))
    MINUS
    --table of skills have
    (SELECT per_id, ks_code have
    FROM has_skill)
)
--count the number of people who need each skill 
SELECT need, count(per_id) people_missing
FROM missing_k 
GROUP BY need


--20. In a local or national crisis, we need to find all the people who once held a 
--    job position of the special job category identifier. List per_id, name, job 
--    position title and the years the person worked (starting year and ending year).
SELECT per_id, first_name, last_name, title, hire_date, leave_date 
FROM person NATURAL JOIN works NATURAL JOIN position  NATURAL JOIN job_category 
WHERE job_cate = job_cate AND job_cate = 'CS005'


--21. Find out (1) the number of the workers whose earnings increased, (2) the number of 
--    those whose earnings decreased, (3) the ratio of (# of earning increased : # of 
--    earning decreased), (4)the average earning changing rate of the workers in a specific 
--    industry group (use attribute “industry group” in table Company). [Hint: earning 
--    change = the sum of a person’s current earnings – the sum of the person’s earning when 
--    he/she was holding his/her the last job position.  For (4), only count the 
--    earning from the specified industry group.]
--(1) the number of the workers whose earnings increased
WITH previous_rate AS (
    SELECT *
    FROM (SELECT works.per_id, position.pay_rate AS prev_rate
        FROM works NATURAL JOIN position CROSS JOIN (
                SELECT per_id
                FROM works 
                WHERE leave_date IS NULL) A 
        WHERE leave_date IS NOT NULL AND A.per_id = works.per_id ))
SELECT COUNT(per_id) AS pay_increase
FROM previous_rate NATURAL JOIN 
            (SELECT per_id, position.pay_rate AS current_rate
            FROM works NATURAL JOIN position
            WHERE works.leave_date IS NULL) A
WHERE prev_rate< current_rate


--(2) the number of those whose earnings decreased
WITH previous_rate AS (
    SELECT *
    FROM (SELECT works.per_id, position.pay_rate AS prev_rate
        FROM works NATURAL JOIN position CROSS JOIN (
                SELECT per_id
                FROM works 
                WHERE leave_date IS NULL) A 
        WHERE leave_date IS NOT NULL AND A.per_id = works.per_id ))
SELECT  COUNT(per_id) AS pay_decrease
FROM previous_rate NATURAL JOIN 
            (SELECT per_id, position.pay_rate AS current_rate
            FROM works NATURAL JOIN position
            WHERE works.leave_date IS NULL) A
WHERE prev_rate > current_rate


--(3)ratio of job pay increasing or decreasing
WITH temp AS(
    SELECT *
    FROM (
        SELECT *
        FROM (SELECT works.per_id, position.pay_rate AS prev_rate
            FROM works NATURAL JOIN position CROSS JOIN (
                SELECT per_id
                FROM works 
                WHERE leave_date IS NULL) A 
            WHERE leave_date IS NOT NULL AND A.per_id = works.per_id )) NATURAL JOIN 
                (SELECT per_id, position.pay_rate AS current_rate
                FROM works NATURAL JOIN position
                WHERE works.leave_date IS NULL) A)
SELECT (numb_increase ||':'||numb_decrease) AS pay_ratio
FROM (
    SELECT (SELECT COUNT(per_id)
            FROM temp
            WHERE current_rate > prev_rate) AS numb_increase , numb_decrease
    FROM (SELECT COUNT(per_id)AS numb_decrease
            FROM temp
            WHERE current_rate < prev_rate))


--(4) the average earning changing rate of the workers in a specific 
--    industry group (use attribute “industry group” in table Company).
WITH temp AS(
    SELECT *
    FROM (
        SELECT *
        FROM (SELECT works.per_id, position.pay_rate AS prev_rate
            FROM sub_ind NATURAL JOIN company NATURAL JOIN works NATURAL JOIN position CROSS JOIN (
                SELECT per_id
                FROM works 
                WHERE leave_date IS NULL) A 
            WHERE leave_date IS NOT NULL AND A.per_id = works.per_id  AND 
                industry_code = '45102010')) NATURAL JOIN 
                (SELECT per_id, position.pay_rate AS current_rate
                FROM works NATURAL JOIN position
                WHERE works.leave_date IS NULL) A)
SELECT  CAST(numb_increase AS INTEGER) / CAST(numb_decrease AS INTEGER) AS rate_of_change
FROM (
    SELECT (SELECT COUNT(per_id)
            FROM temp
            WHERE current_rate > prev_rate) AS numb_increase , numb_decrease
    FROM (SELECT COUNT(per_id)AS numb_decrease
            FROM temp
            WHERE current_rate < prev_rate))


--22. Find all the unemployed people who once held a job position of the given pos_code.
WITH unemployed AS(
    SELECT per_id
    FROM ((SELECT per_id
            FROM works)
            MINUS
            (SELECT per_id
            FROM works 
            WHERE leave_date IS NULL)) 
    )
SELECT * 
FROM unemployed NATURAL JOIN works 
WHERE pos_code = '00125';


--23. Find the leaf-node job categories that have the most openings due to lack of 
--    qualified workers.  If there are many opening positions of a job category
--    but at the same time there are many qualified jobless people.  Then training 
--    can not help fill up this type of job position. What we want to find is the
--    job category that has the largest difference between vacancies (the unfilled 
--    job positions of this category) and the number of jobless people who are qualified 
--    for the job positions of this category. 

--positions with number of openings
WITH open_positions AS(
    SELECT pos_code, COUNT(pos_code) AS numb_open
    FROM position NATURAL JOIN(
    --all jobs
    SELECT pos_code
    FROM position
    MINUS
    --filled jobs
    SELECT pos_code
    FROM works
    WHERE works.leave_date IS null
    )
    GROUP BY pos_code
),
--unemployeed persons
unemployed AS(
    SELECT per_id
    FROM person
    MINUS
    SELECT per_id
    FROM (SELECT per_id
            FROM works
        WHERE works.leave_date IS null)
),
--find the number of qualified people per job
qualified_persons AS(
    SELECT pos_code, COUNT(per_id) AS numb_qual
    FROM open_positions O, unemployed U
    WHERE NOT EXISTS (
        SELECT ks_code
        FROM pos_requires
        WHERE O.pos_code = pos_requires.pos_code
        MINUS
        SELECT ks_code
        FROM has_skill
        WHERE U.per_id = has_skill.per_id
        )
        GROUP BY pos_code
),

differences AS (
    SELECT pos_code, SUM(open_positions.numb_open - qualified_persons.numb_qual) AS diff
    FROM open_positions NATURAL JOIN qualified_persons
    GROUP BY pos_code
)
--select job category with the most differences
SELECT job_cate
FROM differences NATURAL JOIN position
WHERE diff = (SELECT MAX(diff) FROM differences);


--24. If query #13 returns nothing, then find the course sets that their combination covers
--    all the missing knowledge/skills for a person to pursue a pos_code. The considered 
--    course sets will not include more than three courses.  If multiple course sets are found, 
--    list the course sets (with their course IDs) in the order of the ascending order of the 
--    course sets’ total costs.

WITH person_skills AS (
    SELECT ks_code
    FROM has_skill
    WHERE per_id = '10010'),
missing_skills AS (
    SELECT pos_requires.pos_code, pos_requires.ks_code
    FROM pos_requires
    LEFT JOIN person_skills
    ON pos_requires.ks_code = person_skills.ks_code
    WHERE person_skills.ks_code IS NULL
    AND pos_requires.pos_code = '00999'),
course_for_missing_skills AS (
    SELECT course.c_code, missing_skills.ks_code, retail_price
    FROM course
    INNER JOIN section
    ON course.c_code = section.c_code
    INNER JOIN teaches
    ON course.c_code = teaches.c_code
    INNER JOIN missing_skills
    ON teaches.ks_code = missing_skills.ks_code
    WHERE status = 'active'),
course_sets AS (
    SELECT c1.c_code AS course_1,
           c2.c_code AS course_2,
           NULL AS course_3,
           c1.ks_code AS ks_1,
           c2.ks_code AS ks_2,
           NULL AS ks_3,
           ROUND(c1.retail_price + c2.retail_price, 2) AS total_cost
    FROM course_for_missing_skills c1
        INNER JOIN course_for_missing_skills c2
        ON c1.c_code < c2.c_code
    UNION
    SELECT c1.c_code AS course_1,
           c2.c_code AS course_2,
           c3.c_code AS course_3,
           c1.ks_code AS ks_1,
           c2.ks_code AS ks_2,
           c3.ks_code ks_3,
           ROUND(c1.retail_price + c2.retail_price + c3.retail_price, 2) AS total_cost
    FROM course_for_missing_skills c1
        INNER JOIN course_for_missing_skills c2
        ON c1.c_code < c2.c_code
        INNER JOIN course_for_missing_skills c3
        ON c1.c_code < c3.c_code
        AND c2.c_code < c3.c_code),
course_set_for_skill AS (
    SELECT ks_1 AS ks_code, course_1, course_2, course_3, total_cost
    FROM course_sets
    UNION
    SELECT ks_2 AS ks_code, course_1, course_2, course_3, total_cost
    FROM course_sets
    UNION
    SELECT ks_3 AS ks_code, course_1, course_2, course_3, total_cost
    FROM course_sets)
SELECT course_1, course_2, course_3, total_cost
FROM course_set_for_skill
GROUP BY course_1, course_2, course_3, total_cost
HAVING COUNT(DISTINCT ks_code) = (SELECT COUNT(*) 
                                  FROM missing_skills)
ORDER BY total_cost ASC;


--25. Suppose that every job position of the same leaf-node job category requires exactly the 
--    same set of skills. Therefore, you only need to find one position of this leaf-node job 
--    category, and the skills required by this position represent the requirement of skills of 
--    the whole job category. Find the course sets that teach every skill required by the
--    job positions of the job categories found in Query #23. These courses should effectively 
--    help most jobless people become qualified for the jobs with high demands. 

--positions with number of openings
WITH open_positions AS(
    SELECT pos_code, COUNT(pos_code) AS numb_open
    FROM position NATURAL JOIN(
    --all jobs
    SELECT pos_code
    FROM position
    MINUS
    --filled jobs
    SELECT pos_code
    FROM works
    WHERE works.leave_date IS null
    )
    GROUP BY pos_code
),
--unemployeed persons
unemployed AS(
    SELECT per_id
    FROM person
    MINUS
    SELECT per_id
    FROM (SELECT per_id
            FROM works
        WHERE works.leave_date IS null)
),
--find the number of qualified people per job
qualified_persons AS(
    SELECT pos_code, COUNT(per_id) AS numb_qual
    FROM open_positions O, unemployed U
    WHERE NOT EXISTS (
        SELECT ks_code
        FROM pos_requires
        WHERE O.pos_code = pos_requires.pos_code
        MINUS
        SELECT ks_code
        FROM has_skill
        WHERE U.per_id = has_skill.per_id
        )
        GROUP BY pos_code
),

differences AS (
    SELECT pos_code, SUM(open_positions.numb_open - qualified_persons.numb_qual) AS diff
    FROM open_positions NATURAL JOIN qualified_persons
    GROUP BY pos_code
),

--courses and number of jobless people they help
courses AS (
    SELECT T.c_code, COUNT(DISTINCT U.per_id) AS numb_per_course_qual
    FROM teaches T NATURAL JOIN open_positions NATURAL JOIN pos_requires, unemployed U
    WHERE NOT EXISTS(
        --skills required by job category
        SELECT pos_requires.ks_code
        FROM pos_requires INNER JOIN position
        ON pos_requires.pos_code = position.pos_code
        WHERE position.job_cate = (
            SELECT job_cate
            FROM differences NATURAL JOIN position
            WHERE diff = (SELECT MAX(diff) FROM differences))
            MINUS
            --skills of an unemployeed person
            SELECT has_skill.ks_code
            FROM has_skill
            WHERE has_skill.per_id = U.per_id
            MINUS
            --skills a course offers
            (SELECT ks_code
             FROM teaches
             WHERE teaches.c_code = T.c_code)
            )
        GROUP BY T.c_code
        )
SELECT title, c_code, numb_per_course_qual
FROM courses NATURAL JOIN course
WHERE numb_per_course_qual = (SELECT MAX(numb_per_course_qual) FROM courses);


--26. List all the courses, directly or indirectly required, that a person has to take in order 
--    to be qualified for a job position of the given category, according to his/her skills 
--    possessed and courses taken. (Required for graduate students, bonus work for undergraduate 
--    students)

--list all knowledge skills that a person needs for a position
WITH skills_needed AS(
    SELECT ks_code need, c_code
    FROM pos_requires NATURAL JOIN teaches
    WHERE pos_code = '00444'
),
temp AS(--gives skills needed  prereqs
    SELECT course.c_code, prereq 
    FROM skills_needed LEFT OUTER JOIN course 
    ON course.prereq IS NOT NULL 
        AND skills_needed.c_code = course.c_code
    WHERE course.c_code IS NOT NULL
),
temp2 AS(--gives second level of prereqs
    SELECT course.c_code, course.prereq 
    FROM temp LEFT OUTER JOIN course 
    ON course.prereq IS NOT NULL 
        AND temp.prereq = course.c_code
    WHERE course.c_code IS NOT NULL 
),
temp3 AS(--combines skills needed plus the first and second level of prereqs
    (SELECT course.c_code, course.prereq 
    FROM temp2 LEFT OUTER JOIN course 
    ON course.prereq IS NOT NULL 
        AND temp2.prereq = course.c_code
    WHERE course.c_code IS NOT NULL)
    UNION
    --get the first level of prereq
    (SELECT course.c_code, course.prereq 
    FROM temp LEFT OUTER JOIN course 
    ON course.prereq IS NOT NULL 
        AND temp.prereq = course.c_code
    WHERE course.c_code IS NOT NULL)
    UNION
    --get the second level of prereqs
    (SELECT course.c_code, prereq 
    FROM skills_needed LEFT OUTER JOIN course 
    ON course.prereq IS NOT NULL 
        AND skills_needed.c_code = course.c_code
    WHERE course.c_code IS NOT NULL)
    UNION
    SELECT ks_code need, c_code
    FROM pos_requires NATURAL JOIN teaches
    WHERE pos_code = '00444'
),
temp4 AS(--list all prereqs of these courses up to what the person has
    SELECT DISTINCT prereq AS course_id, ks_code AS req_skill
    FROM temp3, teaches
    WHERE prereq = teaches.c_code
)
SELECT course_id, req_skill
FROM temp4,(
    --return all skills required minus skills have
    (SELECT req_skill AS ks_code
    FROM temp4)
    MINUS
    (SELECT ks_code
    FROM has_skill
    WHERE per_id = '10010')
    ) 
WHERE req_skill = ks_code

