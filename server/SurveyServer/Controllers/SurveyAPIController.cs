using System;
using System.Collections.Generic;
using System.Linq;
using System.Text.Json;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using SurveyServer.Data;
using SurveyServer.Models;
using SurveyServer.Scripts;

namespace SurveyServer.Controllers
{
    [Route("[controller]")]
    [ApiController]
    public class SurveyAPIController : ControllerBase
    {
        private readonly ApplicationDbContext _context;
        public SurveyAPIController(ApplicationDbContext context)
        {
            _context = context;
        }
        // GET: SurveyAPI/5
        [HttpGet("{id}", Name = "Get")]
        public string Get(string id)
        {
            var result = _context.Survey.FindAsync(id);
            if (result == null)
            {
                return "Not found";
            }
            Survey survey = result.Result;
            JsonSurvey jsonSurvey = new JsonSurvey();
            jsonSurvey.id = id;
            jsonSurvey.pwd = survey.PatternLock;
            List<JsonQuestion> jsonQuestionList = new List<JsonQuestion>();
            Question[] questionList = _context.Question.Include(q => q.Survey).Where(m => m.RefSurveyID == id).ToArray();
            foreach (Question question in questionList)
            {
                JsonQuestion jsonQuestion = new JsonQuestion();
                jsonQuestionList.Add(jsonQuestion);
                jsonQuestion.question = question.QuestionDescription;
                
                switch (question.QuestionType)
                {
                    case QuestionType.Input:
                        jsonQuestion.type = "edit";
                        jsonQuestion.options = new string[0];
                        break;
                    case QuestionType.Multiple:
                        jsonQuestion.type = "multiple";
                        jsonQuestion.options = question.Options.Split(";");
                        break;
                    case QuestionType.Single:
                        jsonQuestion.type = "single";
                        jsonQuestion.options = question.Options.Split(";");
                        break;
                    default:
                        break;
                }
            }
            jsonSurvey.questions = jsonQuestionList.ToArray();
            string jsonStr = "";
            try
            {
                jsonStr = JsonSerializer.Serialize(jsonSurvey);
            }
            catch (Exception e)
            {
                return "Failed";
            }
            return jsonStr;
        }

        // Post: SurveyAPI/5
        [HttpPost("{id}",Name ="Post")]
        public async Task<IActionResult> Post(string id, [FromBody] JsonElement value)
        {
            
            string str = JsonSerializer.Serialize(value);
            JsonResponse jsonResponse = null;
            try
            {
                jsonResponse = JsonSerializer.Deserialize<JsonResponse>(str);
            }
            catch(Exception e)
            {
                return BadRequest();
            }
            if (jsonResponse==null)
            {
                return BadRequest();
            }
            else if (id != jsonResponse.id)
            {
                return NotFound();
            }
            var survey = await _context.Survey.FindAsync(id);
            if (survey == null)
            {
                return NotFound();
            }

            Response response = new Response();
            int index = 0;
            response.ResponseID = RandomStringBuilder.Create();
            response.IMEI = jsonResponse.IMEI;
            response.Latitude = jsonResponse.Latitude;
            response.Longitude = jsonResponse.Longitude;
            response.RefSurveyID = jsonResponse.id;
            response.Survey = survey;
            response.TimeStamp = jsonResponse.time;
            foreach (string answerStr in jsonResponse.answers)
            {
                Answer answer = new Answer();
                answer.Response = response;
                answer.RefResponseID = response.ResponseID;
                answer.QuestionNum = index;
                answer.Content = answerStr;
                answer.AnswerKey = RandomStringBuilder.Create();
                _context.Add(answer);
                ++index;
            }

            _context.Add(response);
            await _context.SaveChangesAsync();

            return Ok();
        }
        internal class JsonSurvey
        {
            public string id { get; set; }
            public string pwd { get; set; }
            public JsonQuestion[] questions { get; set; }
        }
        
        internal class JsonQuestion
        {
            public string type { get; set; }
            public string question { get; set; }
            public string[] options { get; set; }
        }
        internal class JsonResponse
        {
            public string id { get; set; }
            public long time { get; set; }
            public long Longitude { get; set; }
            public long Latitude { get; set; }
            public string IMEI { get; set; }
            public string[] answers { get; set; }
        }
    }
}
