-- department
CREATE TABLE department (
                            id BIGSERIAL PRIMARY KEY,
                            department_name VARCHAR(100) NOT NULL UNIQUE,
                            use_yn VARCHAR(1) DEFAULT 'Y',
                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- subject
CREATE TABLE subject (
                         id BIGSERIAL PRIMARY KEY,
                         subject_name VARCHAR(100) NOT NULL UNIQUE,
                         subject_category VARCHAR(100) NOT NULL,
                         department_id BIGINT NOT NULL,
                         grade INT NOT NULL,
                         use_yn VARCHAR(1) DEFAULT 'Y',
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         CONSTRAINT fk_subject_department FOREIGN KEY (department_id) REFERENCES department (id)
);

-- exam
CREATE TABLE exam (
                      id BIGSERIAL PRIMARY KEY,
                      subject_id BIGINT NOT NULL,
                      exam_type VARCHAR(20) NOT NULL, -- ATTENDANCE / FINAL / SEASONAL
                      year INT NOT NULL,
                      use_yn VARCHAR(1) DEFAULT 'Y',
                      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                      updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                      CONSTRAINT uq_exam_unique UNIQUE (year, exam_type, subject_id),
                      CONSTRAINT fk_exam_subject FOREIGN KEY (subject_id) REFERENCES subject (id)
);

-- exam_question
CREATE TABLE exam_question (
                               id BIGSERIAL PRIMARY KEY,
                               exam_id BIGINT NOT NULL,
                               question_no INT NOT NULL,
                               question_text VARCHAR(500) NOT NULL,
                               option1 VARCHAR(255) NOT NULL,
                               option2 VARCHAR(255) NOT NULL,
                               option3 VARCHAR(255) NOT NULL,
                               option4 VARCHAR(255) NOT NULL,
                               image_url VARCHAR(500),
                               CONSTRAINT uq_exam_question UNIQUE (exam_id, question_no),
                               CONSTRAINT fk_exam FOREIGN KEY (exam_id) REFERENCES exam (id) ON DELETE CASCADE
);

-- exam_question_answer
CREATE TABLE exam_question_answer (
                                      id BIGSERIAL PRIMARY KEY,
                                      question_id BIGINT NOT NULL,
                                      option_no INT NOT NULL,
                                      CONSTRAINT uq_answer UNIQUE (question_id, option_no),
                                      CONSTRAINT fk_exam_question FOREIGN KEY (question_id) REFERENCES exam_question (id) ON DELETE CASCADE
);

-- notice
CREATE TABLE notice (
                        id BIGSERIAL PRIMARY KEY,
                        title VARCHAR(255) NOT NULL,
                        content TEXT NOT NULL,
                        is_pinned BOOLEAN DEFAULT FALSE,
                        use_yn VARCHAR(1) DEFAULT 'Y',
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- users
CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       email VARCHAR(100) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       role VARCHAR(20) NOT NULL,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
