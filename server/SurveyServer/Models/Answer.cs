using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace SurveyServer.Models
{
    public class Answer
    {
        [Key]
        public string AnswerKey { get; set; }
        [Display(Name = "No.")]
        public int QuestionNum { get; set; }

        [Display(Name = "Answer")]
        public string Content { get; set; }
        
        [ForeignKey("Response"),Display(Name ="Response ID")]
        public string RefResponseID { get; set; }

        public Response Response { get; set; }
    }
}
