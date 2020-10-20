--Stephen Marcel
--Evan Christensen

--1. List a specific company’s workers by names. 
SELECT person.first_name, person.last_name
FROM person NATURAL JOIN works NATURAL JOIN position
WHERE position.comp_name = 'Ninth Ward Programming';

--2. List a specific company’s staff (salary workers) 
--   by salary in descending order.
SELECT pay_rate
FROM position
WHERE comp_name = 'Initech' AND
        pay_type = 'salary'
ORDER BY position.pay_rate DESC;

--3. List the average annual pay (the salary or wage rates multiplying 
--   by 1920 hours) of each company in descending order.
SELECT comp_name,AVG(yearly_rate)
FROM (SELECT pos_code, comp_name, pay_rate*1920 AS yearly_rate 
        FROM position 
        WHERE pay_type = 'hourly'
        UNION
        SELECT pos_code, comp_name, pay_rate AS yearly_rate
        FROM position 
        WHERE pay_type = 'salary')
GROUP BY comp_name;

--4. List the average, maximum and minimum annual pay (total salaries 
--   or wage rates multiplying by 1920 hours) of  each industry (listed 
--   in GICS) in the order of the industry names.
WITH temp AS(
    SELECT gcis.ind_title, yearly_rate
    FROM GCIS NATURAL JOIN sub_ind NATURAL JOIN company NATURAL JOIN
        (SELECT pos_code, pay_rate*1920 AS yearly_rate 
             FROM position 
             WHERE pay_type = 'hourly'
             UNION ALL
             SELECT pos_code, pay_rate AS yearly_rate
             FROM position 
             WHERE pay_type = 'salary') A)

SELECT ind_title, (SELECT AVG(yearly_rate) 
        FROM  temp) AS avg_rate, (SELECT MIN(yearly_rate) AS min_rate
        FROM temp) AS min_rate, max_rate
FROM (SELECT ind_title, MAX(yearly_rate) AS max_rate
        FROM temp
        GROUP BY ind_title)

--5. Find out the biggest employer, industry, and industry group 
--   in terms of number of employees. (Three queries) 
--Biggest employer: referenced ex 3.10 hwk problem
SELECT comp_name
FROM company NATURAL JOIN position NATURAL JOIN works
GROUP BY comp_name
HAVING COUNT(DISTINCT per_id) >= ALL (
        SELECT COUNT(DISTINCT per_id)
        FROM company NATURAL JOIN position NATURAL JOIN works
        GROUP BY comp_name)

--biggest industry
WITH temp AS(
    SELECT parent_code
    FROM GCIS NATURAL JOIN sub_ind NATURAL JOIN company NATURAL JOIN position NATURAL JOIN works
    GROUP BY parent_code
    HAVING COUNT(DISTINCT per_id) >= ALL (
            SELECT COUNT(DISTINCT per_id)
            FROM GCIS NATURAL JOIN sub_ind NATURAL JOIN company NATURAL JOIN position NATURAL JOIN works
            GROUP BY parent_code))
SELECT ind_title
FROM temp CROSS JOIN GCIS 
WHERE temp.parent_code = industry_code

--biggest industry Group.  Does it work?  Probably not.
WITH temp AS(
    SELECT parent_code
    FROM GCIS NATURAL JOIN sub_ind NATURAL JOIN company NATURAL JOIN position NATURAL JOIN works
    GROUP BY parent_code
    HAVING COUNT(DISTINCT per_id) >= ALL (
            SELECT COUNT(DISTINCT per_id)
            FROM GCIS NATURAL JOIN sub_ind NATURAL JOIN company NATURAL JOIN position NATURAL JOIN works
            GROUP BY parent_code))
SELECT ind_title
FROM GCIS A
WHERE A.industry_code = (SELECT GCIS.parent_code
            FROM temp CROSS JOIN GCIS 
            WHERE temp.parent_code = industry_code)

--6. Find out the job distribution among industries by showing the 
--   number of employees in each industry.
--copied number 5 part b
WITH temp AS (
        SELECT parent_code, COUNT(DISTINCT per_id) AS employees
        FROM GCIS NATURAL JOIN sub_ind NATURAL JOIN company NATURAL JOIN position NATURAL JOIN works
        GROUP BY parent_code)
SELECT ind_title, employees
FROM temp CROSS JOIN GCIS
WHERE temp.parent_code = industry_code

--7. Given a person’s identifier, find all the job positions this
--   person is currently holding and worked in the past.
SELECT pos_code, pos_name
FROM works NATURAL JOIN position
WHERE per_id = '1000C';
--seems correct

--8.Given a person’s identifier, list this
--person’s knowledge/skills in a readable format.
SELECT last_name, first_name per_id, skill_title, ks_code
FROM has_skill NATURAL JOIN person
WHERE per_id = '1000B';

--9.Given a person’s identifier, show the distribution of his/her skills by 
--listing the number of skills in each of the cc_code in Table A.
SELECT per_id, skill_type.cc_code, COUNT(cc_code)
FROM has_skill NATURAL JOIN skill_type
WHERE per_id = '1000B'
GROUP BY per_id, cc_code;

--10.List the required knowledge/skills of a given pos_code in a readable format.
SELECT title, ks_code
FROM pos_requires NATURAL JOIN knowledge_skill
WHERE pos_code = '00125';

--11.List the required skill categories of a given job category code in a 
--readable format. 
SELECT job_cate, cc_code, ks_code, skill_title
FROM job_category NATURAL JOIN 
     (core_skill NATURAL JOIN (skill_type NATURAL JOIN knowledge_skill)
WHERE job_cate = 'CS004';

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
SELECT c_code, title
FROM teaches NATURAL JOIN skills_required;

--14.Suppose the skill gap of a worker and the requirement of a desired job 
--position can be covered by one course. Find the cheapest course to make up one’s 
--skill gap by showing the course with the lowest minimum section price.
WITH skills_required AS (
    SELECT ks_code
    FROM pos_requires
    WHERE pos_code = '06238'
    MINUS
    SELECT ks_code
    FROM has_skill 
    WHERE per_id = '10010')
SELECT c_code, title , MIN(price)
FROM section NATURAL JOIN (teaches NATURAL JOIN skills_required)
GROUP BY c_code, title;

--15.Given a person’s identifier, find the job position with the highest pay rate 
--for this person according to his/her skill possession. 
SELECT pos_code, pay_rate
FROM (person NATURAL JOIN has_skill) NATURAL JOIN position
WHERE per_id = '10010'
ORDER BY pay_rate DESC;

--16.Given a position code, list all the names along with the emails of the 
--persons who are qualified for this position. 
SELECT first_name, last_name, email
FROM (person NATURAL JOIN has_skill) NATURAL JOIN pos_requires
WHERE pos_code = '06238';

--17. When a company cannot find any qualified person for a job position, 
--    a secondary solution is to find a person who is almost qualified to the 
--    job position. Make a “missing-k” list that lists people who miss only k
--    skills for a specified pos_code; k< 4. 
WITH skills AS(
    SELECT per_id, skill_title, A.pos_code, A.ks_code
        FROM has_skill, (
            SELECT pos_code, ks_code 
            FROM pos_requires
            WHERE pos_code = '00126') A
        WHERE has_skill.ks_code = A.ks_code)
SELECT first_name, last_name, per_id
FROM person NATURAL JOIN (
        SELECT *
        FROM (SELECT COUNT(pos_code) AS pos
             FROM pos_requires
             WHERE pos_code = '00126'), 
             (SELECT per_id,COUNT(per_id) AS per
             FROM skills
             GROUP BY per_id))
WHERE pos <= per +1;

--18. Suppose there is a new position that has nobody qualified.  List 
--    the persons who miss the least number of skills that are required 
--    by this pos_code and report the “least number”. 
WITH skills AS(
    SELECT  MAX(per) AS total_skills 
    FROM person NATURAL JOIN (
        SELECT *
        FROM (SELECT COUNT(pos_code) AS pos
             FROM pos_requires
             WHERE pos_code = '00126'), 
                (SELECT per_id,COUNT(per_id) AS per
                FROM (SELECT * --per_id, skill_title, A.pos_code, A.ks_code
                    FROM has_skill, (
                        SELECT pos_code, ks_code 
                        FROM pos_requires
                        WHERE pos_code = '00126') A
                    WHERE has_skill.ks_code = A.ks_code)
                GROUP BY per_id)) )
SELECT per_id, first_name, last_name, total_skills, skills_needed
FROM skills CROSS JOIN (
        SELECT *
        FROM (SELECT COUNT(pos_code) AS skills_needed
            FROM pos_requires
            WHERE pos_code = '00126'), 
            (SELECT per_id,COUNT(per_id) AS per
            FROM (SELECT * --per_id, skill_title, A.pos_code, A.ks_code
                    FROM has_skill, (
                        SELECT pos_code, ks_code 
                        FROM pos_requires
                        WHERE pos_code = '00126') A
                    WHERE has_skill.ks_code = A.ks_code)
             GROUP BY per_id))
             NATURAL JOIN person
WHERE total_skills = per

--19. List each of the skill code and the number of people who are missing the skills 
--    and are in the missing-k list for a given position code in the ascending order 
--    of the people counts. 
WITH skills AS(
    SELECT per_id, skill_title, A.pos_code, A.ks_code
        FROM has_skill, (
            SELECT pos_code, ks_code 
            FROM pos_requires
            WHERE pos_code = '00126') A
        WHERE has_skill.ks_code = A.ks_code)
SELECT  per_id, first_name, last_name, ks_code
FROM has_skill NATURAL JOIN person NATURAL JOIN (
        SELECT *
        FROM (SELECT COUNT(pos_code) AS pos
             FROM pos_requires
             WHERE pos_code = '00126'), 
             (SELECT per_id,COUNT(per_id) AS per
             FROM skills
             GROUP BY per_id) CROSS JOIN ((SELECT ks_code 
                FROM skills)))
WHERE pos <= per +1 --AND ks_code = skill_code

--20. In a local or national crisis, we need to find all the people who once held a 
--    job position of the special job category identifier. List per_id, name, job 
--    position title and the years the person worked (starting year and ending year).
SELECT per_id, first_name, last_name, title, hire_date, leave_date 
FROM person NATURAL JOIN works NATURAL JOIN position NATURAL JOIN job_category
WHERE job_cate = cat_code AND job_cate = 'CS005'

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
    SELECT works.per_id
    FROM works CROSS JOIN ((SELECT per_id
            FROM works)
            MINUS
            (SELECT per_id
            FROM works NATURAL JOIN position
            WHERE leave_date IS NULL)) A
    WHERE works.per_id = A.per_id
    )
SELECT * 
FROM unemployed NATURAL JOIN works 
WHERE pos_code = '06238';

--23. Find the leaf-node job categories that have the most openings due to lack of 
--    qualified workers.  If there are many opening positions of a job category
--    but at the same time there are many qualified jobless people.  Then training 
--    can not help fill up this type of job position. What we want to find is the
--    job category that has the largest difference between vacancies (the unfilled 
--    job positions of this category) and the number of jobless people who are qualified 
--    for the job positions of this category. 

--24. If query #13 returns nothing, then find the course sets that their combination covers
--    all the missing knowledge/skills for a person to pursue a pos_code. The considered 
--    course sets will not include more than three courses.  If multiple course sets are found, 
--    list the course sets (with their course IDs) in the order of the ascending order of the 
--    course sets’ total costs.

--25. Suppose that every job position of the same leaf-node job category requires exactly the 
--    same set of skills. Therefore, you only need to find one position of this leaf-node job 
--    category, and the skills required by this position represent the requirement of skills of 
--    the whole job category. Find the course sets that teach every skill required by the
--    job position ns of the job categories found in Query #23. These courses should effective 
--    help most jobless people become qualified for the jobs with high demands. 

--26. List all the courses, directly or indirectly required, that a person has to take in order 
--    to be qualified for a job position of the given category, according to his/her skills 
--    possessed and courses taken. (Required for graduate students, bonus work for undergraduate 
--    students)















