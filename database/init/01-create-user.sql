-- GPU 관리 시스템용 사용자 생성
CREATE USER gpu_admin IDENTIFIED BY gpu_password;

-- 권한 부여
GRANT CONNECT, RESOURCE, DBA TO gpu_admin;
GRANT CREATE SESSION TO gpu_admin;
GRANT CREATE TABLE TO gpu_admin;
GRANT CREATE SEQUENCE TO gpu_admin;
GRANT CREATE VIEW TO gpu_admin;
GRANT CREATE PROCEDURE TO gpu_admin;
GRANT CREATE TRIGGER TO gpu_admin;

-- 테이블스페이스 권한
ALTER USER gpu_admin DEFAULT TABLESPACE USERS;
ALTER USER gpu_admin QUOTA UNLIMITED ON USERS;

-- 배치 작업을 위한 추가 권한
GRANT CREATE JOB TO gpu_admin;
GRANT MANAGE SCHEDULER TO gpu_admin;

-- 모니터링을 위한 시스템 뷰 접근 권한
GRANT SELECT ON V_$SESSION TO gpu_admin;
GRANT SELECT ON V_$SQL TO gpu_admin;
GRANT SELECT ON V_$SQLAREA TO gpu_admin;